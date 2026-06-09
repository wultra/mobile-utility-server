/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2023  Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wultra.app.mobileutilityserver.rest.service;

import com.wultra.app.mobileutilityserver.database.model.*;
import com.wultra.app.mobileutilityserver.database.repo.*;
import com.wultra.app.mobileutilityserver.rest.errorhandling.AppException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.AppNotFoundException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.DomainNameCertificateMismatchException;
import com.wultra.app.mobileutilityserver.rest.model.converter.CertificateConverter;
import com.wultra.app.mobileutilityserver.rest.model.converter.MobileAppConverter;
import com.wultra.app.mobileutilityserver.rest.model.entity.Domain;
import com.wultra.app.mobileutilityserver.rest.model.entity.MobileApplication;
import com.wultra.app.mobileutilityserver.rest.model.enums.Platform;
import com.wultra.app.mobileutilityserver.rest.model.request.*;
import com.wultra.app.mobileutilityserver.rest.model.response.*;
import com.wultra.security.powerauth.crypto.lib.model.exception.CryptoProviderException;
import com.wultra.security.powerauth.crypto.lib.model.exception.GenericCryptoException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.stream.Collectors;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Administration related methods.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class AdminService {

    private final MobileAppRepository mobileAppRepository;
    private final CertificateRepository certificateRepository;
    private final MobileDomainRepository mobileDomainRepository;
    private final LocalizedTextRepository localizedTextRepository;
    private final MobileAppVersionRepository mobileAppVersionRepository;

    private final CertificateConverter certificateConverter;
    private final MobileAppConverter mobileAppConverter;

    private final CryptographicOperationsService cryptographicOperationsService;

    /**
     * Create a new application and generate signing keypair to it
     * @param request Request object with the information about the application.
     * @return Application details.
     * @throws AppException In case application of given name already exists.
     */
    public ApplicationDetailResponse createApplication(CreateApplicationRequest request) throws AppException {
        try {
            final String name = request.getName();
            final String displayName = request.getDisplayName();

            // Validate app existence
            final boolean existsByName = mobileAppRepository.existsByName(name);
            if (existsByName) {
                throw new AppException("Application with name already exists: " + name);
            }

            // Prepare signing keypair data
            final KeyPair keyPair = cryptographicOperationsService.generateKeyPair();
            final String privateKeyString = cryptographicOperationsService.convertPrivateKeyToBase64(keyPair.getPrivate());
            final String publicKeyString = cryptographicOperationsService.convertPublicKeyToBase64(keyPair.getPublic());

            // Prepare and store the entity
            final MobileAppEntity mobileAppEntity = new MobileAppEntity();
            mobileAppEntity.setName(name);
            mobileAppEntity.setDisplayName(displayName);
            mobileAppEntity.setSigningPrivateKey(privateKeyString);
            mobileAppEntity.setSigningPublicKey(publicKeyString);

            final MobileAppEntity savedMobileAppEntity = mobileAppRepository.save(mobileAppEntity);

            return mobileAppConverter.convertMobileApp(savedMobileAppEntity);
        } catch (CryptoProviderException | GenericCryptoException e) {
            throw new AppException("Error while generating cryptographic keys", e);
        }
    }

    @Transactional(readOnly = true)
    public ApplicationListResponse applicationList() {
        final Iterable<MobileAppEntity> mobileApps = mobileAppRepository.findAll();
        final ApplicationListResponse response = new ApplicationListResponse();
        for (MobileAppEntity mobileAppEntity : mobileApps) {
            final MobileApplication app = new MobileApplication();
            app.setName(mobileAppEntity.getName());
            app.setDisplayName(mobileAppEntity.getDisplayName());
            response.getApplications().add(app);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public ApplicationDetailResponse applicationDetail(String name) {
        final MobileAppEntity mobileAppEntity = mobileAppRepository.findFirstByName(name);
        return mobileAppConverter.convertMobileApp(mobileAppEntity);
    }

    public CertificateDetailResponse createApplicationCertificate(String appName, CreateApplicationCertificateDirectRequest request) throws AppNotFoundException {
        final String domain = request.getDomain();
        final String pem = request.getPem();
        final String fingerprint = request.getFingerprint();
        final Long expires = request.getExpires();
        final Integer depth = request.getDepth();

        final MobileAppEntity mobileAppEntity = mobileAppRepository.findFirstByName(appName);
        if (mobileAppEntity == null) {
            throw new AppNotFoundException(appName);
        }

        final List<CertificateEntity> certificateEntityOptional = certificateRepository.findFirstByAppNameAndDomain(appName, domain);
        if (!certificateEntityOptional.isEmpty()) {
            for (CertificateEntity cert : certificateEntityOptional) {
                if (fingerprint.equalsIgnoreCase(cert.getFingerprint())) {
                    final CertificateDetailResponse response = certificateConverter.convertCertificateDetailResponse(cert);
                    logger.info("", kv("action", "addOrRefreshCertificate"), kv("state", "noChange"), kv("appName", appName), kv("domain", domain));
                    return response;
                }
            }
        }

        MobileDomainEntity domainEntity = mobileDomainRepository.findFirstByAppNameAndDomain(mobileAppEntity.getName(), domain);
        if (domainEntity == null) {
            domainEntity = new MobileDomainEntity();
            domainEntity.setApp(mobileAppEntity);
            domainEntity.setDomain(domain);
            domainEntity.setSslPinningRequired(true);
            domainEntity = mobileDomainRepository.save(domainEntity);
        }

        final CertificateEntity certificateEntity = new CertificateEntity();
        certificateEntity.setDomain(domainEntity);
        certificateEntity.setPem(pem);
        certificateEntity.setFingerprint(fingerprint);
        certificateEntity.setExpires(expires);
        certificateEntity.setDepth(depth);

        final CertificateEntity savedCertificateEntity = certificateRepository.save(certificateEntity);

        final CertificateDetailResponse response = certificateConverter.convertCertificateDetailResponse(savedCertificateEntity);
        logger.info("", kv("action", "addOrRefreshCertificate"), kv("state", "succeeded"), kv("appName", appName), kv("domain", domain));
        return response;
    }

    public CertificateDetailResponse createApplicationCertificate(final String appName, final CreateApplicationCertificatePemRequest request) throws IOException,
            NoSuchAlgorithmException, AppNotFoundException, DomainNameCertificateMismatchException {

        final String domain = request.getDomain();
        final String pem = request.getPem();
        final int depth = request.getDepth() != null ? request.getDepth() : 0;

        final PEMParser pemParser = new PEMParser(new StringReader(pem));
        final Object pemInfo = pemParser.readObject();
        if (pemInfo == null) {
            throw new IOException("PemParser read null, appName: " + appName);
        }
        pemParser.close();

        final X509CertificateHolder x509Cert = (X509CertificateHolder) pemInfo;
        if (depth == 0) {
            cryptographicOperationsService.verifyHostname(domain, x509Cert);
        }

        final long notAfter = x509Cert.getNotAfter().getTime() / 1000;

        final CreateApplicationCertificateDirectRequest innerRequest = new CreateApplicationCertificateDirectRequest();
        innerRequest.setDomain(domain);
        innerRequest.setPem(pem);
        innerRequest.setFingerprint(cryptographicOperationsService.computeSHA256Hash(x509Cert.getEncoded()));
        innerRequest.setExpires(notAfter);
        innerRequest.setDepth(depth);

        return this.createApplicationCertificate(appName, innerRequest);
    }

    public CertificateDetailResponse createApplicationCertificate(final String appName, final CreateApplicationCertificateRequest request) throws IOException,
            NoSuchAlgorithmException, AppNotFoundException, CertificateEncodingException, DomainNameCertificateMismatchException {
        final String domain = request.getDomain();

        final X509Certificate cert = fetchCertificate(domain);
        final String certPem = cryptographicOperationsService.certificateToPem(cert);
        logger.info("", kv("action", "createCertificate"), kv("state", "initiated"), kv("appName", appName), kv("domain", domain));

        final CreateApplicationCertificatePemRequest innerRequest = new CreateApplicationCertificatePemRequest();
        innerRequest.setDomain(domain);
        innerRequest.setPem(certPem);
        innerRequest.setDepth(0);

        return this.createApplicationCertificate(appName, innerRequest);
    }

    private static X509Certificate fetchCertificate(final String domain) throws IOException {
        final SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
        try (final SSLSocket socket = (SSLSocket) factory.createSocket(domain, 443)) {
            socket.setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.3"});
            socket.startHandshake();
            final Certificate[] certs = socket.getSession().getPeerCertificates();
            return (X509Certificate) certs[0];
        }
    }

    public void deleteCertificate(String appName, String domain, String fingerprint) {
        final MobileDomainEntity mobileDomainEntity = mobileDomainRepository.findFirstByAppNameAndDomain(appName, domain);
        if (mobileDomainEntity == null) {
            return;
        }
        final List<CertificateEntity> certificates = mobileDomainEntity.getCertificates();
        for (CertificateEntity certificate: certificates) {
            if (certificate.getFingerprint().equalsIgnoreCase(fingerprint)) {
                certificates.remove(certificate);
                mobileDomainRepository.save(mobileDomainEntity);
                return;
            }
        }
    }

    public void deleteDomain(String appName, String domain) {
        mobileDomainRepository.deleteByAppNameAndDomain(appName, domain);
    }

    public void deleteExpiredCertificates() {
        certificateRepository.deleteAllByExpiresBefore(new Date().getTime() / 1000);
    }

    @Transactional(readOnly = true)
    public ApplicationVersionListResponse applicationVersionList(final String applicationName) {
        logger.debug("Looking for application versions", kv("applicationName", applicationName));
        return convertVersions(mobileAppVersionRepository.findByApplicationName(applicationName));
    }

    @Transactional(readOnly = true)
    public ApplicationVersionDetailResponse applicationVersionDetail(final String applicationName, final Long id) {
        logger.debug("Looking for application version", kv("applicationName", applicationName), kv("id", id));
        return convert(mobileAppVersionRepository.findById(id)
                .orElseThrow(() -> new ConstraintViolationException("Version not found, ID: " + id, Collections.emptySet())));
    }

    public ApplicationVersionDetailResponse createApplicationVersion(final String applicationName, final CreateApplicationVersionRequest request) {
        logger.debug("Creating application version", kv("applicationName", applicationName));
        validateCreateApplicationVersion(applicationName, request);

        final MobileAppVersionEntity entity = convert(request);
        final MobileAppEntity app = mobileAppRepository.findFirstByName(applicationName);
        if (app == null) {
            throw new ConstraintViolationException("Application not found, name: " + applicationName, Collections.emptySet());
        }
        entity.setApp(app);
        final var result = mobileAppVersionRepository.save(entity);
        return convert(result);
    }

    private void validateCreateApplicationVersion(final String applicationName, final CreateApplicationVersionRequest request) {
        final MobileAppVersionEntity.Platform platform = convert(request.getPlatform());
        final Integer majorOsVersion = request.getMajorOsVersion();
        final Optional<MobileAppVersionEntity> applicationVersion;

        if (majorOsVersion != null) {
            applicationVersion = mobileAppVersionRepository
                    .findFirstByApplicationNameAndPlatformAndMajorOsVersion(applicationName, platform, majorOsVersion);
        } else {
            applicationVersion = mobileAppVersionRepository
                    .findFirstByApplicationNameAndPlatform(applicationName, platform);
        }

        if (applicationVersion.isPresent()) {
            throw new ConstraintViolationException(
                    "Application version already exists, applicationName=%s, platform=%s, majorOsVersion=%d"
                            .formatted(applicationName, platform, majorOsVersion),
                    Collections.emptySet());
        }
    }

    public void deleteApplicationVersion(final String applicationName, final Long id) {
        logger.debug("Deleting application version", kv("applicationName", applicationName), kv("id", id));
        mobileAppVersionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TextListResponse textList() {
        return convertTexts(localizedTextRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TextDetailResponse textDetail(final String key, final String language) {
        final var id = new LocalizedTextEntity.LocalizedTextId(key, language);
        logger.debug("Looking for text", kv("id", id));
        return convert(localizedTextRepository.findById(id)
                .orElseThrow(() -> new ConstraintViolationException("Text not found, ID: " + id, Collections.emptySet())));
    }

    public TextDetailResponse createText(final CreateTextRequest request) {
        logger.debug("Creating text", kv("key", request.getMessageKey()), kv("language", request.getLanguage()));
        final var result = localizedTextRepository.save(convert(request));
        return convert(result);
    }

    public void deleteText(final String key, final String language) {
        final var id = new LocalizedTextEntity.LocalizedTextId(key, language);
        logger.debug("Deleting text", kv("id", id));
        localizedTextRepository.deleteById(id);
    }

    private static LocalizedTextEntity convert(final CreateTextRequest source) {
        final var target = new LocalizedTextEntity();
        target.setMessageKey(source.getMessageKey());
        target.setLanguage(source.getLanguage());
        target.setText(source.getText());
        return target;
    }

    private static TextListResponse convertTexts(final Iterable<LocalizedTextEntity> source) {
        final var target = new TextListResponse();
        source.forEach(it ->
                target.getTexts().add(convert(it)));
        return target;
    }

    private static TextDetailResponse convert(final LocalizedTextEntity source) {
        final var target = new TextDetailResponse();
        target.setMessageKey(source.getMessageKey());
        target.setLanguage(source.getLanguage());
        target.setText(source.getText());
        return target;
    }

    private static ApplicationVersionListResponse convertVersions(final Iterable<MobileAppVersionEntity> source) {
        final var target = new ApplicationVersionListResponse();
        source.forEach(it ->
                target.getApplicationVersions().add(convert(it)));
        return target;
    }

    private static ApplicationVersionDetailResponse convert(final MobileAppVersionEntity source) {
        final var target = new ApplicationVersionDetailResponse();
        target.setId(source.getId());
        target.setRequiredVersion(source.getRequiredVersion());
        target.setSuggestedVersion(source.getSuggestedVersion());
        target.setMessageKey(source.getMessageKey());
        target.setPlatform(convert(source.getPlatform()));
        target.setMajorOsVersion(source.getMajorOsVersion());
        return target;
    }

    private static Platform convert(final MobileAppVersionEntity.Platform source) {
        return switch(source) {
            case ANDROID -> Platform.ANDROID;
            case IOS -> Platform.IOS;
        };
    }

    private static MobileAppVersionEntity convert(final CreateApplicationVersionRequest source) {
        final var target = new MobileAppVersionEntity();
        target.setRequiredVersion(source.getRequiredVersion());
        target.setSuggestedVersion(source.getSuggestedVersion());
        target.setMessageKey(source.getMessageKey());
        target.setMajorOsVersion(source.getMajorOsVersion());
        target.setPlatform(convert(source.getPlatform()));
        return target;
    }

    private static MobileAppVersionEntity.Platform convert(final Platform source) {
        return switch(source) {
            case ANDROID -> MobileAppVersionEntity.Platform.ANDROID;
            case IOS -> MobileAppVersionEntity.Platform.IOS;
        };
    }

    /**
     * Sets the pinning required flag to all domains of the given application. If the domain exists and is present in the supplied set,
     * its pinning required flag is set to false. If the domain exists and is not present, its flag is set to true. Supplied domains
     * that do not exist yet are created with the pinning required flag set to false.
     *
     * @param applicationName      application name
     * @param pinningBypassDomains domains to set the pinning required flag to false
     * @return resulting pinning required state of all domains of the given application
     */
    public SavePinningBypassDomainsResponse savePinningBypassDomains(final String applicationName, final Set<String> pinningBypassDomains) throws AppNotFoundException {
        final MobileAppEntity app = mobileAppRepository.findFirstByName(applicationName);
        if (app == null) {
            throw new AppNotFoundException(applicationName);
        }

        final List<MobileDomainEntity> appDomains = app.getDomains();

        final Set<String> existingDomainNames = appDomains.stream()
                .map(MobileDomainEntity::getDomain)
                .collect(Collectors.toSet());

        // set pinning required flag to the existing domains
        for (final MobileDomainEntity appDomain : appDomains) {
            final boolean isPinningBypassDomain = pinningBypassDomains.contains(appDomain.getDomain());
            appDomain.setSslPinningRequired(!isPinningBypassDomain);
        }

        final Set<String> newBypassDomainNames = new HashSet<>(pinningBypassDomains);
        newBypassDomainNames.removeAll(existingDomainNames);

        // create bypass domains that do not exist yet
        for (final String newBypassDomainName : newBypassDomainNames) {
            final MobileDomainEntity newBypassDomain = new MobileDomainEntity();
            newBypassDomain.setDomain(newBypassDomainName);
            newBypassDomain.setSslPinningRequired(false);
            newBypassDomain.setApp(app);

            appDomains.add(newBypassDomain);
        }

        final List<Domain> resultingDomains = new ArrayList<>();
        mobileDomainRepository.saveAll(appDomains).forEach(appDomain -> resultingDomains.add(mobileAppConverter.convertDomain(appDomain)));

        return new SavePinningBypassDomainsResponse(Collections.unmodifiableList(resultingDomains));
    }
}

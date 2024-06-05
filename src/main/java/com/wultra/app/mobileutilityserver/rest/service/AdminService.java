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
import com.wultra.app.mobileutilityserver.rest.model.converter.CertificateConverter;
import com.wultra.app.mobileutilityserver.rest.model.converter.MobileAppConverter;
import com.wultra.app.mobileutilityserver.rest.model.entity.MobileApplication;
import com.wultra.app.mobileutilityserver.rest.model.enums.Platform;
import com.wultra.app.mobileutilityserver.rest.model.request.*;
import com.wultra.app.mobileutilityserver.rest.model.response.*;
import io.getlime.security.powerauth.crypto.lib.model.exception.CryptoProviderException;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private final CacheService cacheService;

    @Autowired
    public AdminService(MobileAppRepository mobileAppRepository,
                        CertificateRepository certificateRepository,
                        MobileDomainRepository mobileDomainRepository,
                        CertificateConverter certificateConverter,
                        MobileAppConverter mobileAppConverter,
                        CryptographicOperationsService cryptographicOperationsService, CacheService cacheService) {
        this.mobileAppRepository = mobileAppRepository;
        this.certificateRepository = certificateRepository;
        this.mobileDomainRepository = mobileDomainRepository;
        this.certificateConverter = certificateConverter;
        this.mobileAppConverter = mobileAppConverter;
        this.cryptographicOperationsService = cryptographicOperationsService;
        this.cacheService = cacheService;
    }

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

            cacheService.notifyEvictAppCache(name);

            return mobileAppConverter.convertMobileApp(savedMobileAppEntity);
        } catch (CryptoProviderException e) {
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

        final MobileAppEntity mobileAppEntity = mobileAppRepository.findFirstByName(appName);
        if (mobileAppEntity == null) {
            throw new AppNotFoundException(appName);
        }

        final List<CertificateEntity> certificateEntityOptional = certificateRepository.findFirstByAppNameAndDomain(appName, domain);
        if (!certificateEntityOptional.isEmpty()) {
            for (CertificateEntity cert : certificateEntityOptional) {
                if (fingerprint.equalsIgnoreCase(cert.getFingerprint())) {
                    final CertificateDetailResponse response = certificateConverter.convertCertificateDetailResponse(cert);
                    logger.info("Certificate up-to-date: {}", response);
                    return response;
                }
            }
        }

        MobileDomainEntity domainEntity = mobileDomainRepository.findFirstByAppNameAndDomain(mobileAppEntity.getName(), domain);
        if (domainEntity == null) {
            domainEntity = new MobileDomainEntity();
            domainEntity.setApp(mobileAppEntity);
            domainEntity.setDomain(domain);
            domainEntity = mobileDomainRepository.save(domainEntity);
        }

        final CertificateEntity certificateEntity = new CertificateEntity();
        certificateEntity.setDomain(domainEntity);
        certificateEntity.setPem(pem);
        certificateEntity.setFingerprint(fingerprint);
        certificateEntity.setExpires(expires);

        final CertificateEntity savedCertificateEntity = certificateRepository.save(certificateEntity);

        cacheService.notifyEvictCertificateCache(appName);

        final CertificateDetailResponse response = certificateConverter.convertCertificateDetailResponse(savedCertificateEntity);
        logger.info("Certificate refreshed: {}", response);
        return response;
    }

    public CertificateDetailResponse createApplicationCertificate(String appName, CreateApplicationCertificatePemRequest request) throws IOException, NoSuchAlgorithmException, AppNotFoundException {

        final String domain = request.getDomain();
        final String pem = request.getPem();

        final PEMParser pemParser = new PEMParser(new StringReader(pem));
        final Object pemInfo = pemParser.readObject();
        if (pemInfo == null) {
            throw new IOException("PemParser read null, appName: " + appName);
        }
        pemParser.close();
        final X509CertificateHolder x509Cert = (X509CertificateHolder) pemInfo;
        final long notAfter = x509Cert.getNotAfter().getTime() / 1000;

        final CreateApplicationCertificateDirectRequest innerRequest = new CreateApplicationCertificateDirectRequest();
        innerRequest.setDomain(domain);
        innerRequest.setPem(pem);
        innerRequest.setFingerprint(cryptographicOperationsService.computeSHA256Hash(x509Cert.getEncoded()));
        innerRequest.setExpires(notAfter);

        return this.createApplicationCertificate(appName, innerRequest);
    }

    public CertificateDetailResponse createApplicationCertificate(String appName, CreateApplicationCertificateRequest request) throws IOException, NoSuchAlgorithmException, AppNotFoundException, CertificateEncodingException {
        final String domain = request.getDomain();

        final X509Certificate cert = fetchCertificate(domain);
        final String certPem = cryptographicOperationsService.certificateToPem(cert);

        final CreateApplicationCertificatePemRequest innerRequest = new CreateApplicationCertificatePemRequest();
        innerRequest.setDomain(domain);
        innerRequest.setPem(certPem);

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
                cacheService.notifyEvictCertificateCache(appName);
                return;
            }
        }
    }

    public void deleteDomain(String appName, String domain) {
        mobileDomainRepository.deleteByAppNameAndDomain(appName, domain);
        cacheService.notifyEvictCertificateCache(appName);
    }

    public void deleteExpiredCertificates() {
        certificateRepository.deleteAllByExpiresBefore(new Date().getTime() / 1000);
        cacheService.invalidateCertificateCache();
    }

    @Transactional(readOnly = true)
    public ApplicationVersionListResponse applicationVersionList(final String applicationName) {
        logger.debug("Looking for application versions name: {}", applicationName);
        return convertVersions(mobileAppVersionRepository.findByApplicationName(applicationName));
    }

    @Transactional(readOnly = true)
    public ApplicationVersionDetailResponse applicationVersionDetail(final String applicationName, final Long id) {
        logger.debug("Looking for application version name: {}, ID: {}", applicationName, id);
        return convert(mobileAppVersionRepository.findById(id)
                .orElseThrow(() -> new ConstraintViolationException("Version not found, ID: " + id, Collections.emptySet())));
    }

    public ApplicationVersionDetailResponse createApplicationVersion(final String applicationName, final CreateApplicationVersionRequest request) {
        logger.debug("Creating application version for name: {}", applicationName);
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
        logger.debug("Deleting application version name: {}, ID: {}", applicationName, id);
        mobileAppVersionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TextListResponse textList() {
        return convertTexts(localizedTextRepository.findAll());
    }

    @Transactional(readOnly = true)
    public TextDetailResponse textDetail(final String key, final String language) {
        final var id = new LocalizedTextEntity.LocalizedTextId(key, language);
        logger.debug("Looking for text ID: {}", id);
        return convert(localizedTextRepository.findById(id)
                .orElseThrow(() -> new ConstraintViolationException("Text not found, ID: " + id, Collections.emptySet())));
    }

    public TextDetailResponse createText(final CreateTextRequest request) {
        logger.debug("Creating text key: {}, language: {}", request.getMessageKey(), request.getLanguage());
        final var result = localizedTextRepository.save(convert(request));
        return convert(result);
    }

    public void deleteText(final String key, final String language) {
        final var id = new LocalizedTextEntity.LocalizedTextId(key, language);
        logger.debug("Deleting text ID: {}", id);
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
}

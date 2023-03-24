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

import com.wultra.app.mobileutilityserver.database.model.CertificateEntity;
import com.wultra.app.mobileutilityserver.database.model.MobileAppEntity;
import com.wultra.app.mobileutilityserver.database.model.MobileDomainEntity;
import com.wultra.app.mobileutilityserver.database.repo.CertificateRepository;
import com.wultra.app.mobileutilityserver.database.repo.MobileAppRepository;
import com.wultra.app.mobileutilityserver.database.repo.MobileDomainRepository;
import com.wultra.app.mobileutilityserver.rest.errorhandling.AppException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.AppNotFoundException;
import com.wultra.app.mobileutilityserver.rest.model.converter.CertificateConverter;
import com.wultra.app.mobileutilityserver.rest.model.converter.MobileAppConverter;
import com.wultra.app.mobileutilityserver.rest.model.entity.MobileApplication;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationCertificateDirectRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationCertificatePemRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationCertificateRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationRequest;
import com.wultra.app.mobileutilityserver.rest.model.response.ApplicationDetailResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.ApplicationListResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.CertificateDetailResponse;
import io.getlime.security.powerauth.crypto.lib.model.exception.CryptoProviderException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import java.util.Date;
import java.util.List;

/**
 * Administration related methods.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Service
@Slf4j
public class AdminService {

    private final MobileAppRepository mobileAppRepository;
    private final CertificateRepository certificateRepository;
    private final MobileDomainRepository mobileDomainRepository;

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
    @Transactional
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

    @Transactional
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

    @Transactional
    public ApplicationDetailResponse applicationDetail(String name) {
        final MobileAppEntity mobileAppEntity = mobileAppRepository.findFirstByName(name);
        return mobileAppConverter.convertMobileApp(mobileAppEntity);
    }

    @Transactional
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

    @Transactional
    public CertificateDetailResponse createApplicationCertificate(String appName, CreateApplicationCertificatePemRequest request) throws IOException, NoSuchAlgorithmException, AppNotFoundException {

        final String domain = request.getDomain();
        final String pem = request.getPem();

        final PEMParser pemParser = new PEMParser(new StringReader(pem));
        final Object pemInfo = pemParser.readObject();
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

    @Transactional
    public CertificateDetailResponse createApplicationCertificate(String appName, CreateApplicationCertificateRequest request) throws IOException, NoSuchAlgorithmException, AppNotFoundException, CertificateEncodingException {
        final String domain = request.getDomain();

        final SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
        final SSLSocket socket = (SSLSocket) factory.createSocket(domain, 443);
        socket.startHandshake();
        final Certificate[] certs = socket.getSession().getPeerCertificates();
        final X509Certificate cert = (X509Certificate) certs[0];
        socket.close();

        final String certPem = cryptographicOperationsService.certificateToPem(cert);

        final CreateApplicationCertificatePemRequest innerRequest = new CreateApplicationCertificatePemRequest();
        innerRequest.setDomain(domain);
        innerRequest.setPem(certPem);

        return this.createApplicationCertificate(appName, innerRequest);

    }

    @Transactional
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

    @Transactional
    public void deleteDomain(String appName, String domain) {
        mobileDomainRepository.deleteByAppNameAndDomain(appName, domain);
        cacheService.notifyEvictCertificateCache(appName);
    }

    @Transactional
    public void deleteExpiredCertificates() {
        certificateRepository.deleteAllByExpiresBefore(new Date().getTime() / 1000);
        cacheService.invalidateCertificateCache();
    }

}

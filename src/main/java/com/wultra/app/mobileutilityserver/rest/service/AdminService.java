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

import com.wultra.app.mobileutilityserver.database.model.MobileApp;
import com.wultra.app.mobileutilityserver.database.model.MobileDomainEntity;
import com.wultra.app.mobileutilityserver.database.model.SslPinningFingerprintDbEntity;
import com.wultra.app.mobileutilityserver.database.repo.MobileAppRepository;
import com.wultra.app.mobileutilityserver.database.repo.MobileDomainRepository;
import com.wultra.app.mobileutilityserver.database.repo.SslPinningFingerprintRepository;
import com.wultra.app.mobileutilityserver.rest.errorhandling.AppException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.AppNotFoundException;
import com.wultra.app.mobileutilityserver.rest.model.converter.MobileAppConverter;
import com.wultra.app.mobileutilityserver.rest.model.converter.SslPinningFingerprintConverter;
import com.wultra.app.mobileutilityserver.rest.model.entity.MobileApplication;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationFingerprintAutoRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationFingerprintPemRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationFingerprintRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationRequest;
import com.wultra.app.mobileutilityserver.rest.model.response.ApplicationDetailResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.ApplicationListResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.FingerprintDetailResponse;
import com.wultra.app.mobileutilityserver.util.CryptoUtils;
import io.getlime.security.powerauth.crypto.lib.model.exception.CryptoProviderException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
    private final SslPinningFingerprintRepository sslPinningFingerprintRepository;
    private final MobileDomainRepository mobileDomainRepository;

    private final SslPinningFingerprintConverter fingerprintConverter;
    private final MobileAppConverter mobileAppConverter;

    private final CryptoUtils cryptoUtils;

    @Autowired
    public AdminService(MobileAppRepository mobileAppRepository,
                        SslPinningFingerprintRepository sslPinningFingerprintRepository,
                        MobileDomainRepository mobileDomainRepository,
                        SslPinningFingerprintConverter fingerprintConverter,
                        MobileAppConverter mobileAppConverter, CryptoUtils cryptoUtils) {
        this.mobileAppRepository = mobileAppRepository;
        this.sslPinningFingerprintRepository = sslPinningFingerprintRepository;
        this.mobileDomainRepository = mobileDomainRepository;
        this.fingerprintConverter = fingerprintConverter;
        this.mobileAppConverter = mobileAppConverter;
        this.cryptoUtils = cryptoUtils;
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
            final KeyPair keyPair = cryptoUtils.generateKeyPair();
            final String privateKeyString = cryptoUtils.convertPrivateKeyToBase64(keyPair.getPrivate());
            final String publicKeyString = cryptoUtils.convertPublicKeyToBase64(keyPair.getPublic());

            // Prepare and store the entity
            final MobileApp mobileApp = new MobileApp();
            mobileApp.setName(name);
            mobileApp.setDisplayName(displayName);
            mobileApp.setSigningPrivateKey(privateKeyString);
            mobileApp.setSigningPublicKey(publicKeyString);

            final MobileApp savedMobileApp = mobileAppRepository.save(mobileApp);

            return mobileAppConverter.convertMobileApp(savedMobileApp);
        } catch (CryptoProviderException e) {
            throw new AppException("Error while generating cryptographic keys", e);
        }
    }

    @Transactional
    public ApplicationListResponse applicationList() {
        final Iterable<MobileApp> mobileApps = mobileAppRepository.findAll();
        final ApplicationListResponse response = new ApplicationListResponse();
        for (MobileApp mobileApp: mobileApps) {
            final MobileApplication app = new MobileApplication();
            app.setName(mobileApp.getName());
            app.setDisplayName(mobileApp.getDisplayName());
            response.getApplications().add(app);
        }
        return response;
    }

    @Transactional
    public ApplicationDetailResponse applicationDetail(String name) {
        final MobileApp mobileApp = mobileAppRepository.findFirstByName(name);
        return mobileAppConverter.convertMobileApp(mobileApp);
    }

    @Transactional
    public FingerprintDetailResponse createApplicationFingerprint(CreateApplicationFingerprintRequest request) throws AppNotFoundException {
        final String appName = request.getAppName();
        final String domain = request.getDomain();
        final String fingerprint = request.getFingerprint();
        final Long expires = request.getExpires();

        final MobileApp mobileApp = mobileAppRepository.findFirstByName(appName);
        if (mobileApp == null) {
            throw new AppNotFoundException(appName);
        }

        final List<SslPinningFingerprintDbEntity> fingerprintEntityOptional = sslPinningFingerprintRepository.findFirstByAppNameAndDomain(appName, domain);
        if (!fingerprintEntityOptional.isEmpty()) {
            for (SslPinningFingerprintDbEntity f : fingerprintEntityOptional) {
                if (fingerprint.equalsIgnoreCase(f.getFingerprint())) {
                    final FingerprintDetailResponse response = fingerprintConverter.convertFingerprintDetailResponse(f);
                    logger.info("Fingerprint up-to-date: {}", response);
                    return response;
                }
            }
        }

        MobileDomainEntity domainEntity = mobileDomainRepository.findFirstByDomainAndAppName(domain, mobileApp.getName());
        if (domainEntity == null) {
            domainEntity = new MobileDomainEntity();
            domainEntity.setApp(mobileApp);
            domainEntity.setDomain(domain);
            domainEntity = mobileDomainRepository.save(domainEntity);
        }

        final SslPinningFingerprintDbEntity fingerprintEntity = new SslPinningFingerprintDbEntity();
        fingerprintEntity.setDomain(domainEntity);
        fingerprintEntity.setFingerprint(fingerprint);
        fingerprintEntity.setExpires(expires);

        final SslPinningFingerprintDbEntity savedFingerprintEntity = sslPinningFingerprintRepository.save(fingerprintEntity);

        final FingerprintDetailResponse response = fingerprintConverter.convertFingerprintDetailResponse(savedFingerprintEntity);
        logger.info("Fingerprint refreshed: {}", response);
        return response;
    }

    @Transactional
    public FingerprintDetailResponse createApplicationFingerprint(CreateApplicationFingerprintPemRequest request) throws IOException, NoSuchAlgorithmException, AppNotFoundException {

        final String appName = request.getAppName();
        final String domain = request.getDomain();
        final String pem = request.getPem();

        final PEMParser pemParser = new PEMParser(new StringReader(pem));
        final Object pemInfo = pemParser.readObject();
        pemParser.close();
        final X509CertificateHolder x509Cert = (X509CertificateHolder) pemInfo;
        final long notAfter = x509Cert.getNotAfter().getTime() / 1000;

        final CreateApplicationFingerprintRequest innerRequest = new CreateApplicationFingerprintRequest();
        innerRequest.setAppName(appName);
        innerRequest.setDomain(domain);
        innerRequest.setFingerprint(cryptoUtils.computeSHA256Signature(pem.getBytes(StandardCharsets.UTF_8)));
        innerRequest.setExpires(notAfter);

        return this.createApplicationFingerprint(innerRequest);
    }

    @Transactional
    public FingerprintDetailResponse createApplicationFingerprint(CreateApplicationFingerprintAutoRequest request) throws IOException, NoSuchAlgorithmException, AppNotFoundException, CertificateEncodingException {
        final String domain = request.getDomain();
        final String appName = request.getAppName();

        final SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
        final SSLSocket socket = (SSLSocket) factory.createSocket(domain, 443);
        socket.startHandshake();
        final Certificate[] certs = socket.getSession().getPeerCertificates();
        final X509Certificate cert = (X509Certificate) certs[0];
        socket.close();

        final String certPem = cryptoUtils.certificateToPem(cert);
        logger.info("Certificate read for app: {}, domain: {}\n{}", appName, domain, certPem);

        final CreateApplicationFingerprintPemRequest innerRequest = new CreateApplicationFingerprintPemRequest();
        innerRequest.setAppName(appName);
        innerRequest.setDomain(domain);
        innerRequest.setPem(certPem);

        return this.createApplicationFingerprint(innerRequest);

    }

    @Transactional
    public void deleteFingerprint(String domain, String fingerprint) {
        sslPinningFingerprintRepository.deleteAllByDomainAndFingerprint(domain, fingerprint);
    }

    @Transactional
    public void deleteExpiredFingerprints() {
        sslPinningFingerprintRepository.deleteAllByExpiresBefore(new Date().getTime() / 1000);
    }

    @Scheduled(fixedRateString = "${mobile-utility-server.scheduling.update}")
    @Transactional
    public void refreshFingerprints() {
        // Delete expired fingerprints
        sslPinningFingerprintRepository.deleteAllByExpiresBefore(new Date().getTime() / 1000);

        // Update fingerprints for domains
        final Iterable<MobileDomainEntity> domainEntities = mobileDomainRepository.findAll();
        for (MobileDomainEntity domain : domainEntities) {
            try {
                final CreateApplicationFingerprintAutoRequest request = new CreateApplicationFingerprintAutoRequest();
                request.setAppName(domain.getApp().getName());
                request.setDomain(domain.getDomain());
                createApplicationFingerprint(request);
            } catch (AppNotFoundException | CertificateEncodingException | IOException | NoSuchAlgorithmException e) {
                logger.error("Exception occurred when refreshing fingerprint: {}", e.getMessage());
                logger.debug("Exception details:", e);
            }
        }
    }

}

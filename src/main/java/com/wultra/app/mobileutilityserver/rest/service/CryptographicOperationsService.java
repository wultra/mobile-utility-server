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

import io.getlime.security.powerauth.crypto.lib.config.PowerAuthConfiguration;
import io.getlime.security.powerauth.crypto.lib.generator.KeyGenerator;
import io.getlime.security.powerauth.crypto.lib.model.exception.CryptoProviderException;
import io.getlime.security.powerauth.crypto.lib.model.exception.GenericCryptoException;
import io.getlime.security.powerauth.crypto.lib.util.KeyConvertor;
import io.getlime.security.powerauth.crypto.lib.util.SignatureUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Service with various cryptographic helper utils.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Service
@Slf4j
public class CryptographicOperationsService {

    private static final String SECURE_RANDOM_ALGORITHM_NAME = "DEFAULT";

    private final KeyGenerator keyGenerator;
    private final KeyConvertor keyConvertor;

    private final SignatureUtils signatureUtils;

    private final SecureRandom secureRandom;

    @Autowired
    public CryptographicOperationsService(KeyGenerator keyGenerator, KeyConvertor keyConvertor, SignatureUtils signatureUtils) {
        this.keyGenerator = keyGenerator;
        this.keyConvertor = keyConvertor;
        this.signatureUtils = signatureUtils;
        this.secureRandom = secureRandom();
    }

    /**
     * Get the strong secure random available in the system. Try using Bouncy Castle first with a fallback to new
     * secure random (log warning).
     *
     * @return Secure random instance.
     */
    private SecureRandom secureRandom() {
        try {
            return SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM_NAME, PowerAuthConfiguration.CRYPTO_PROVIDER_NAME);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            logger.warn("Unable to register strong random number generator: {}", e.getMessage());
            logger.debug("Exception details: ", e);
            return new SecureRandom();
        }
    }

    /**
     * Compute SHA256 hash of provided data and encode the hash in Base64.
     * @param data Provided data.
     * @return Hash of provided data as Base64 string.
     * @throws NoSuchAlgorithmException In case algorithm does not exist.
     */
    public String computeSHA256Hash(byte[] data) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        return Base64.getEncoder().encodeToString(md.digest());
    }

    /**
     * Generate EC keypair.
     * @return EC keypair.
     * @throws CryptoProviderException Cryptographic provider failed to generate the key pair.
     */
    public KeyPair generateKeyPair() throws CryptoProviderException {
        return keyGenerator.generateKeyPair();
    }

    /**
     * Convert private key to Base64 encoded string.
     * @param privateKey Private key to be encoded.
     * @return Base64 encoded value of the private key.
     */
    public String convertPrivateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(keyConvertor.convertPrivateKeyToBytes(privateKey));
    }

    /**
     * Convert public key to Base64 encoded string.
     * @param publicKey Public key to be encoded.
     * @return Base64 encoded value of the public key.
     * @throws CryptoProviderException In case the public key is not valid.
     */
    public String convertPublicKeyToBase64(PublicKey publicKey) throws CryptoProviderException {
        return Base64.getEncoder().encodeToString(keyConvertor.convertPublicKeyToBytes(publicKey));
    }

    /**
     * Convert certificate to PEM format.
     * @param cert Certificate to be encoded.
     * @return PEM encoded certificate.
     */
    public String certificateToPem(final X509Certificate cert) throws CertificateEncodingException {
        final StringWriter stringWriter = new StringWriter();
        try (final JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
            pemWriter.writeObject(cert);
        } catch (IOException e) {
            throw new CertificateEncodingException(e);
        }
        return stringWriter.toString();
    }

    /**
     * Compute Base64-encoded ECDSA signature with the private key that is provided as Base64.
     * @param signatureBase Signature data.
     * @param privateKeyBase64 Private key encoded in Base64.
     * @return Base64-encoded ECDSA signature.
     * @throws GenericCryptoException In case cryptographic calculation fails.
     * @throws InvalidKeyException In case provided private key is invalid.
     * @throws CryptoProviderException In case cryptographic provider fails.
     * @throws InvalidKeySpecException In case provided private key spec is invalid.
     */
    public String computeECDSASignature(byte[] signatureBase, String privateKeyBase64) throws GenericCryptoException, InvalidKeyException, CryptoProviderException, InvalidKeySpecException {
        final PrivateKey privateKey = keyConvertor.convertBytesToPrivateKey(Base64.getDecoder().decode(privateKeyBase64));
        final byte[] ecdsaSignature = signatureUtils.computeECDSASignature(signatureBase, privateKey);
        return Base64.getEncoder().encodeToString(ecdsaSignature);
    }
}

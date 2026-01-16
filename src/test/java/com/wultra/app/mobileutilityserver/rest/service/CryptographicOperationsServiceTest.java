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

import com.wultra.app.mobileutilityserver.rest.errorhandling.DomainNameCertificateMismatchException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static com.wultra.app.mobileutilityserver.utils.Certificates.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test for {@link CryptographicOperationsService}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
class CryptographicOperationsServiceTest {

    private final CryptographicOperationsService tested = new CryptographicOperationsService(null, null, null);

    @BeforeAll
    static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    void testCertificateToPem() throws Exception {
        final ByteArrayInputStream in = new ByteArrayInputStream(GENERIC_CERT.getBytes());
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(in);

        final String result = tested.certificateToPem(x509Certificate);

        assertEquals(GENERIC_CERT, result);
    }

    @Test
    void testVerifyHostname_matchCert_cn() throws Exception {
        testCertificateMatches("domain.com", CN_ONLY_CERT);
    }

    @Test
    void testVerifyHostname_matchCert_wildcardCn() throws Exception {
        testCertificateMatches("sub.domain.com", WILDCARD_CN_ONLY_CERT);
    }

    @Test
    void testVerifyHostname_matchCert_singleSan() throws Exception {
        testCertificateMatches("domain.com", SINGLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_matchCert_multipleSan() throws Exception {
        testCertificateMatches("domain2.com", MULTIPLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_matchCert_wildcardSingleSan() throws Exception {
        testCertificateMatches("sub.domain.com", WILDCARD_SINGLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_matchCert_wildcardMultipleSan() throws Exception {
        testCertificateMatches("sub.domain2.com", WILDCARD_MULTIPLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_matchCert_sanOnly() throws Exception {
        testCertificateMatches("domain.com", SAN_ONLY_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_exactCn() {
        testCertificateDoesNotMatch("non-matching.com", CN_ONLY_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_wildcardCn() {
        testCertificateDoesNotMatch("sub.non-matching.com", WILDCARD_CN_ONLY_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_wildcardCnSubdomainTooDeep() {
        testCertificateDoesNotMatch("sub.sub.domain.com", WILDCARD_CN_ONLY_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_exactSan() {
        testCertificateDoesNotMatch("non-matching.com", SINGLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_wildcardSan() {
        testCertificateDoesNotMatch("sub.non-matching.com", WILDCARD_SINGLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_wildcardSanSubdomainTooDeep() {
        testCertificateDoesNotMatch("sub.sub.domain.com", WILDCARD_SINGLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_multipleSan() {
        testCertificateDoesNotMatch("non-matching.com", MULTIPLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_wildcardMultipleSan() {
        testCertificateDoesNotMatch("sub.non-matching.com", WILDCARD_MULTIPLE_SAN_CERT);
    }

    @Test
    void testVerifyHostname_noMatch_wildcardMultipleSanSubdomainTooDeep() {
        testCertificateDoesNotMatch("sub.sub.domain2.com", WILDCARD_MULTIPLE_SAN_CERT);
    }

    private void testCertificateMatches(final String domainName, final String certificate) throws Exception {
        tested.verifyHostname(domainName, getCertificateFromPem(certificate));
    }

    private void testCertificateDoesNotMatch(final String domainName, final String certificate) {
        assertThrows(DomainNameCertificateMismatchException.class, () -> tested.verifyHostname(domainName, getCertificateFromPem(certificate)));
    }

    private X509CertificateHolder getCertificateFromPem(final String pem) throws IOException {
        final PEMParser pemParser = new PEMParser(new StringReader(pem));
        return (X509CertificateHolder) pemParser.readObject();
    }
}

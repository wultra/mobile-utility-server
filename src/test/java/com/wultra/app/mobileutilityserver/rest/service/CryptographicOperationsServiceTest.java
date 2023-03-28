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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link CryptographicOperationsService}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
class CryptographicOperationsServiceTest {

    @Test
    void testCertificateToPem() throws Exception {
        final String cert = """
                -----BEGIN CERTIFICATE-----
                MIIEQTCCAymgAwIBAgIBATANBgkqhkiG9w0BAQUFADCBkzEaMBgGA1UEAxMRTW9u
                a2V5IE1hY2hpbmUgQ0ExCzAJBgNVBAYTAlVLMREwDwYDVQQIEwhTY290bGFuZDEQ
                MA4GA1UEBxMHR2xhc2dvdzEcMBoGA1UEChMTbW9ua2V5bWFjaGluZS5jby51azEl
                MCMGCSqGSIb3DQEJARYWY2FAbW9ua2V5bWFjaGluZS5jby51azAeFw0wNTAzMDYy
                MzI4MjJaFw0wNjAzMDYyMzI4MjJaMIGvMQswCQYDVQQGEwJVSzERMA8GA1UECBMI
                U2NvdGxhbmQxEDAOBgNVBAcTB0dsYXNnb3cxGzAZBgNVBAoTEk1vbmtleSBNYWNo
                aW5lIEx0ZDElMCMGA1UECxMcT3BlbiBTb3VyY2UgRGV2ZWxvcG1lbnQgTGFiLjEU
                MBIGA1UEAxMLTHVrZSBUYXlsb3IxITAfBgkqhkiG9w0BCQEWEmx1a2VAbW9ua2V5
                bWFjaGluZTBcMA0GCSqGSIb3DQEBAQUAA0sAMEgCQQDItxZr07mm65ttYH7RMaVo
                VeMCq4ptfn+GFFEk4+54OkDuh1CHlk87gEc1jx3ZpQPJRTJx31z3YkiAcP+RDzxr
                AgMBAAGjggFIMIIBRDAJBgNVHRMEAjAAMBEGCWCGSAGG+EIBAQQEAwIHgDALBgNV
                HQ8EBAMCBeAwHQYDVR0OBBYEFG7mW1czzw4vFcL03+wUvvvPVFY8MIHABgNVHSME
                gbgwgbWAFKt47K8QG4qbH8exJY8WKPIXmq02oYGZpIGWMIGTMRowGAYDVQQDExFN
                b25rZXkgTWFjaGluZSBDQTELMAkGA1UEBhMCVUsxETAPBgNVBAgTCFNjb3RsYW5k
                MRAwDgYDVQQHEwdHbGFzZ293MRwwGgYDVQQKExNtb25rZXltYWNoaW5lLmNvLnVr
                MSUwIwYJKoZIhvcNAQkBFhZjYUBtb25rZXltYWNoaW5lLmNvLnVrggEAMDUGCWCG
                SAGG+EIBBAQoFiZodHRwczovL21vbmtleW1hY2hpbmUuY28udWsvY2EtY3JsLnBl
                bTANBgkqhkiG9w0BAQUFAAOCAQEAZ961bEgm2rOq6QajRLeoljwXDnt0S9BGEWL4
                PMU2FXDog9aaPwfmZ5fwKaSebwH4HckTp11xwe/D9uBZJQ74Uf80UL9z2eo0GaSR
                nRB3QPZfRvop0I4oPvwViKt3puLsi9XSSJ1w9yswnIf89iONT7ZyssPg48Bojo8q
                lcKwXuDRBWciODK/xWhvQbaegGJ1BtXcEHtvNjrUJLwSMDSr+U5oUYdMohG0h1iJ
                R+JQc49I33o2cTc77wfEWLtVdXAyYY4GSJR6VfgvV40x85ItaNS3HHfT/aXU1x4m
                W9YQkWlA6t0blGlC+ghTOY1JbgWnEfXMmVgg9a9cWaYQ+NQwqA==
                -----END CERTIFICATE-----
                """;

        final ByteArrayInputStream in = new ByteArrayInputStream(cert.getBytes());
        final CertificateFactory cf = CertificateFactory.getInstance("X.509");
        final X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(in);

        Security.addProvider(new BouncyCastleProvider());

        final CryptographicOperationsService tested = new CryptographicOperationsService(null, null, null);

        final String result = tested.certificateToPem(x509Certificate);

        assertEquals(cert, result);
    }
}

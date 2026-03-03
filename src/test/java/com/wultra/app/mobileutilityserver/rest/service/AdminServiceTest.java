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

import com.wultra.app.mobileutilityserver.rest.errorhandling.AppNotFoundException;
import com.wultra.app.mobileutilityserver.rest.errorhandling.DomainNameCertificateMismatchException;
import com.wultra.app.mobileutilityserver.rest.model.entity.Domain;
import com.wultra.app.mobileutilityserver.rest.model.enums.Platform;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationCertificatePemRequest;
import com.wultra.app.mobileutilityserver.rest.model.request.CreateApplicationVersionRequest;
import com.wultra.app.mobileutilityserver.rest.model.response.CertificateDetailResponse;
import com.wultra.app.mobileutilityserver.rest.model.response.SavePinningBypassDomainsResponse;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.wultra.app.mobileutilityserver.utils.Certificates.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link AdminService}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql
class AdminServiceTest {

    @Autowired
    private AdminService tested;

    @Test
    void testCreateApplicationVersion_successful() {
        final CreateApplicationVersionRequest request = new CreateApplicationVersionRequest();
        request.setPlatform(Platform.ANDROID);
        request.setSuggestedVersion("1.2.3");

        final var result = tested.createApplicationVersion("test-app", request);

        assertNotNull(result.getId());
    }

    @Test
    void testCreateApplicationVersion_alreadyExists() {
        final CreateApplicationVersionRequest request = new CreateApplicationVersionRequest();
        request.setPlatform(Platform.IOS);
        request.setSuggestedVersion("1.2.3");
        request.setMajorOsVersion(11);

        final var result = assertThrows(ConstraintViolationException.class, () -> tested.createApplicationVersion("test-app", request));

        assertEquals("Application version already exists, applicationName=test-app, platform=IOS, majorOsVersion=11", result.getMessage());
    }

    @Test
    void testCreateApplicationVersion_alreadyExists_majorVersionNull() {
        final CreateApplicationVersionRequest request = new CreateApplicationVersionRequest();
        request.setPlatform(Platform.IOS);
        request.setSuggestedVersion("1.2.3");
        request.setMajorOsVersion(null);

        final var result = assertThrows(ConstraintViolationException.class, () -> tested.createApplicationVersion("test-app", request));

        assertEquals("Application version already exists, applicationName=test-app, platform=IOS, majorOsVersion=null", result.getMessage());
    }

    @Test
    void testSavePinningBypassDomains_successful() throws AppNotFoundException {
        final SavePinningBypassDomainsResponse result = tested.savePinningBypassDomains("test-app", Set.of("example.com", "non-existent.com"));

        final Map<String, Domain> resultDomains = result.domains().stream().collect(Collectors.toMap(Domain::getName, Function.identity()));
        assertEquals(2, resultDomains.size());
        assertFalse(resultDomains.get("example.com").getSslPinningRequired());
        assertTrue(resultDomains.get("test.com").getSslPinningRequired());
        assertNull(resultDomains.get("non-existent.com"));
    }

    @Test
    void testSavePinningBypassDomains_emptyDomainsPassed() throws AppNotFoundException {
        final SavePinningBypassDomainsResponse result = tested.savePinningBypassDomains("test-app", Set.of());

        final Map<String, Domain> resultDomains = result.domains().stream().collect(Collectors.toMap(Domain::getName, Function.identity()));
        assertEquals(2, resultDomains.size());
        assertTrue(resultDomains.get("example.com").getSslPinningRequired());
        assertTrue(resultDomains.get("test.com").getSslPinningRequired());
    }

    @Test
    void testSavePinningBypassDomains_noDomainsInDb() throws AppNotFoundException {
        final SavePinningBypassDomainsResponse result = tested.savePinningBypassDomains("empty-app", Set.of("example.com"));

        assertEquals(0, result.domains().size());
    }

    @Test
    void testSavePinningBypassDomains_appNotExists() {
        assertThrows(AppNotFoundException.class, () -> tested.savePinningBypassDomains("non-existent-app", Set.of("example.com")));
    }

    @Test
    void testCreateApplicationCertificate_successful() throws Exception {
        final CreateApplicationCertificatePemRequest request = new CreateApplicationCertificatePemRequest();
        request.setPem(SINGLE_SAN_CERT);
        request.setDomain("domain.com");
        request.setDepth(1);

        final CertificateDetailResponse result = tested.createApplicationCertificate("test-app", request);

        assertEquals("domain.com", result.getName());
        assertEquals(SINGLE_SAN_CERT, result.getPem());
        assertEquals("7O5ldDPsl+pVo4Hy5RzhTDNfqBBrOoMyzMFBYK+UQMo=", result.getFingerprint());
        assertEquals(1800620222L, result.getExpires());
        assertEquals(1, result.getDepth());
    }

    @Test
    void testCreateApplicationCertificate_noDepthSpecified() throws Exception {
        final CreateApplicationCertificatePemRequest request = new CreateApplicationCertificatePemRequest();
        request.setPem(SINGLE_SAN_CERT);
        request.setDomain("domain.com");

        final CertificateDetailResponse result = tested.createApplicationCertificate("test-app", request);

        assertEquals("domain.com", result.getName());
        assertEquals(0, result.getDepth());
    }

    @Test
    void testCreateApplicationCertificate_domainNameMismatch() {
        final CreateApplicationCertificatePemRequest request = new CreateApplicationCertificatePemRequest();
        request.setPem(SINGLE_SAN_CERT);
        request.setDomain("non-matching.com");
        request.setDepth(0);

        assertThrows(DomainNameCertificateMismatchException.class, () -> tested.createApplicationCertificate("test-app", request));
    }

    @Test
    void testCreateApplicationCertificate_noDomainNameCheckForNonZeroDepth() throws Exception {
        final CreateApplicationCertificatePemRequest request = new CreateApplicationCertificatePemRequest();
        request.setPem(SINGLE_SAN_CERT);
        request.setDomain("non-matching.com");
        request.setDepth(1);

        final CertificateDetailResponse result = tested.createApplicationCertificate("test-app", request);

        assertEquals("non-matching.com", result.getName());
        assertEquals(1, result.getDepth());
    }
}

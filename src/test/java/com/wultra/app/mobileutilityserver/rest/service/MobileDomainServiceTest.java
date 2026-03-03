/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2026 Wultra s.r.o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wultra.app.mobileutilityserver.rest.service;


import com.wultra.app.mobileutilityserver.database.model.MobileDomainEntity;
import com.wultra.app.mobileutilityserver.database.repo.MobileDomainRepository;
import com.wultra.app.mobileutilityserver.rest.model.converter.CertificateConverter;
import com.wultra.app.mobileutilityserver.rest.model.converter.MobileAppConverter;
import com.wultra.app.mobileutilityserver.rest.model.entity.Domain;
import com.wultra.app.mobileutilityserver.rest.model.entity.DomainsConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

/**
 * Tests of {@link MobileDomainService}.
 *
 * @author Pavel Sindelar, pavel.sindelar@wultra.com
 */
@ExtendWith(MockitoExtension.class)
class MobileDomainServiceTest {

    @Mock
    private MobileDomainRepository mobileDomainRepository;

    private MobileDomainService mobileDomainService;

    @BeforeEach
    void setUp() {
        mobileDomainService = new MobileDomainService(mobileDomainRepository, new MobileAppConverter(new CertificateConverter()));
    }

    @Test
    void testGetDomainsConfig_successful() {
        final MobileDomainEntity domainEntity1 = new MobileDomainEntity();
        domainEntity1.setId(1L);
        domainEntity1.setDomain("example.com");
        domainEntity1.setSslPinningRequired(true);

        final MobileDomainEntity domainEntity2 = new MobileDomainEntity();
        domainEntity2.setId(2L);
        domainEntity2.setDomain("test.com");
        domainEntity2.setSslPinningRequired(false);

        final Domain domain1 = new Domain();
        domain1.setName("example.com");
        domain1.setSslPinningRequired(true);

        final Domain domain2 = new Domain();
        domain2.setName("test.com");
        domain2.setSslPinningRequired(false);

        final String applicationName = "test-app";

        when(mobileDomainRepository.findAllByAppName(eq(applicationName))).thenReturn(List.of(domainEntity1, domainEntity2));

        final DomainsConfig result = mobileDomainService.getDomainsConfig(applicationName);

        assertNotNull(result);
        assertTrue(result.sslPinningRequiredForUnlisted());
        assertEquals(2, result.domains().size());
        assertEquals(domain1, result.domains().get(0));
        assertEquals(domain2, result.domains().get(1));
    }

    @Test
    void testGetDomainsConfig_emptyDomains() {
        final String applicationName = "test-app";
        when(mobileDomainRepository.findAllByAppName(eq(applicationName))).thenReturn(List.of());

        final DomainsConfig result = mobileDomainService.getDomainsConfig(applicationName);

        assertNotNull(result);
        assertTrue(result.sslPinningRequiredForUnlisted());
        assertTrue(result.domains().isEmpty());
    }
}

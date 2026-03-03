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
package com.wultra.app.mobileutilityserver.rest.model.converter;


import com.wultra.app.mobileutilityserver.database.model.CertificateEntity;
import com.wultra.app.mobileutilityserver.database.model.MobileAppEntity;
import com.wultra.app.mobileutilityserver.database.model.MobileDomainEntity;
import com.wultra.app.mobileutilityserver.rest.model.entity.Domain;
import com.wultra.app.mobileutilityserver.rest.model.entity.DomainWithCertificates;
import com.wultra.app.mobileutilityserver.rest.model.entity.FullCertificateInfo;
import com.wultra.app.mobileutilityserver.rest.model.response.ApplicationDetailResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests of {@link MobileAppConverter}.
 *
 * @author Pavel Sindelar, pavel.sindelar@wultra.com
 */
class MobileAppConverterTest {

    private final MobileAppConverter tested = new MobileAppConverter(new CertificateConverter());

    @Test
    void testConvertMobileApp_withValidEntity() {
        final MobileAppEntity mobileAppEntity = createMobileAppEntity();
        final FullCertificateInfo fullCertificateInfo = createFullCertificateInfo();

        final ApplicationDetailResponse result = tested.convertMobileApp(mobileAppEntity);

        assertNotNull(result);
        assertEquals("test-app", result.getName());
        assertEquals("Test Application", result.getDisplayName());
        assertEquals("PUBLIC_KEY_123", result.getPublicKey());
        assertEquals(1, result.getDomains().size());

        final DomainWithCertificates domain = result.getDomains().get(0);
        assertEquals("example.com", domain.getName());
        assertTrue(domain.getSslPinningRequired());
        assertEquals(1, domain.getCertificates().size());
        assertEquals(fullCertificateInfo, domain.getCertificates().get(0));
    }

    @Test
    void testConvertDomainWithCertificates_withValidEntity() {
        final MobileDomainEntity mobileDomainEntity = createMobileDomainEntity();
        final FullCertificateInfo fullCertificateInfo = createFullCertificateInfo();

        final DomainWithCertificates result = tested.convertDomainWithCertificates(mobileDomainEntity);

        assertNotNull(result);
        assertEquals("example.com", result.getName());
        assertTrue(result.getSslPinningRequired());
        assertEquals(1, result.getCertificates().size());
        assertEquals(fullCertificateInfo, result.getCertificates().get(0));
    }

    @Test
    void testConvertDomain_withValidEntity() {
        final MobileDomainEntity mobileDomainEntity = createMobileDomainEntity();

        final Domain result = tested.convertDomain(mobileDomainEntity);

        assertNotNull(result);
        assertEquals("example.com", result.getName());
        assertTrue(result.getSslPinningRequired());
    }

    private static MobileAppEntity createMobileAppEntity() {
        final MobileAppEntity mobileAppEntity = new MobileAppEntity();
        mobileAppEntity.setName("test-app");
        mobileAppEntity.setDisplayName("Test Application");
        mobileAppEntity.setSigningPublicKey("PUBLIC_KEY_123");

        final MobileDomainEntity domain = createMobileDomainEntity();
        mobileAppEntity.getDomains().add(domain);

        return mobileAppEntity;
    }

    private static MobileDomainEntity createMobileDomainEntity() {
        final MobileDomainEntity mobileDomainEntity = new MobileDomainEntity();
        mobileDomainEntity.setDomain("example.com");
        mobileDomainEntity.setSslPinningRequired(true);

        final CertificateEntity certificateEntity = new CertificateEntity();
        certificateEntity.setPem("PEM_DATA");
        certificateEntity.setFingerprint("FINGERPRINT_123");
        certificateEntity.setExpires(1234567890L);
        certificateEntity.setDepth(2);
        mobileDomainEntity.getCertificates().add(certificateEntity);

        return mobileDomainEntity;
    }

    private static FullCertificateInfo createFullCertificateInfo() {
        final FullCertificateInfo fullCertificateInfo = new FullCertificateInfo();
        fullCertificateInfo.setPem("PEM_DATA");
        fullCertificateInfo.setFingerprint("FINGERPRINT_123");
        fullCertificateInfo.setExpires(1234567890L);
        fullCertificateInfo.setDepth(2);
        return fullCertificateInfo;
    }
}

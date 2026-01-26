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
import com.wultra.app.mobileutilityserver.database.model.MobileDomainEntity;
import com.wultra.app.mobileutilityserver.rest.model.entity.CertificateFingerprint;
import com.wultra.app.mobileutilityserver.rest.model.entity.FullCertificateInfo;
import com.wultra.app.mobileutilityserver.rest.model.response.CertificateDetailResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests of {@link CertificateConverter}
 *
 * @author Pavel Sindelar, pavel.sindelar@wultra.com
 */
class CertificateConverterTest {

    private static final CertificateEntity CERTIFICATE_ENTITY = createCertificateEntity();

    private final CertificateConverter tested = new CertificateConverter();

    @Test
    void testConvertFrom_withValidEntity() {
        final FullCertificateInfo result = tested.convertFrom(CERTIFICATE_ENTITY);

        assertNotNull(result);
        assertEquals("PEM_DATA", result.getPem());
        assertEquals("FINGERPRINT_123", result.getFingerprint());
        assertEquals(1234567890L, result.getExpires());
        assertEquals(2, result.getDepth());
    }

    @Test
    void testConvertNamedCertificateFrom_withValidEntity() {
        final CertificateFingerprint result = tested.convertNamedCertificateFrom(CERTIFICATE_ENTITY);

        assertNotNull(result);
        assertEquals("example.com", result.getName());
        assertEquals("FINGERPRINT_123", result.getFingerprint());
        assertEquals(1234567890L, result.getExpires());
        assertEquals(2, result.getDepth());
    }

    @Test
    void testConvertCertificateDetailResponse_withValidEntity() {
        final CertificateDetailResponse result = tested.convertCertificateDetailResponse(CERTIFICATE_ENTITY);

        assertNotNull(result);
        assertEquals("example.com", result.getName());
        assertEquals("PEM_DATA", result.getPem());
        assertEquals("FINGERPRINT_123", result.getFingerprint());
        assertEquals(1234567890L, result.getExpires());
        assertEquals(2, result.getDepth());
    }

    private static CertificateEntity createCertificateEntity() {
        final CertificateEntity entity = new CertificateEntity();
        entity.setPem("PEM_DATA");
        entity.setFingerprint("FINGERPRINT_123");
        entity.setExpires(1234567890L);
        entity.setDepth(2);

        final MobileDomainEntity domain = new MobileDomainEntity();
        domain.setDomain("example.com");
        entity.setDomain(domain);

        return entity;
    }
}

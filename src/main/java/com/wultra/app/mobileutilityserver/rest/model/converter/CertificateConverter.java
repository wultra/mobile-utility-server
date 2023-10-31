/*
 * Wultra Mobile Utility Server
 * Copyright (C) 2020  Wultra s.r.o.
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
package com.wultra.app.mobileutilityserver.rest.model.converter;

import com.wultra.app.mobileutilityserver.database.model.CertificateEntity;
import com.wultra.app.mobileutilityserver.rest.model.entity.CertificateFingerprint;
import com.wultra.app.mobileutilityserver.rest.model.entity.FullCertificateInfo;
import com.wultra.app.mobileutilityserver.rest.model.response.CertificateDetailResponse;
import org.springframework.stereotype.Component;

/**
 * Converter class for SSL certificate entity conversion.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Component
public class CertificateConverter {

    /**
     * Convert a database representation of a certificate to Admin REST API representation.
     * @param source SSL certificate model in DB.
     * @return SSL certificate model in REST API.
     */
    public FullCertificateInfo convertFrom(CertificateEntity source) {
        if (source == null) {
            return null;
        }
        final FullCertificateInfo destination = new FullCertificateInfo();
        destination.setPem(source.getPem());
        destination.setFingerprint(source.getFingerprint());
        destination.setExpires(source.getExpires());
        return destination;
    }

    /**
     * Convert a database representation of a certificate to REST API representation with named fingerprint.
     * @param source SSL certificate model in DB.
     * @return SSL fingerprint model in REST API.
     */
    public CertificateFingerprint convertNamedCertificateFrom(CertificateEntity source) {
        if (source == null) {
            return null;
        }
        final CertificateFingerprint destination = new CertificateFingerprint();
        destination.setName(source.getDomain().getDomain());
        destination.setFingerprint(source.getFingerprint());
        destination.setExpires(source.getExpires());
        return destination;
    }

    /**
     * Convert a database representation of a certificate to REST API representation used in admin.
     * @param source SSL certificate model in DB.
     * @return SSL certificate model in admin REST API.
     */
    public CertificateDetailResponse convertCertificateDetailResponse(CertificateEntity source) {
        if (source == null) {
            return null;
        }
        final CertificateDetailResponse destination = new CertificateDetailResponse();
        destination.setName(source.getDomain().getDomain());
        destination.setPem(source.getPem());
        destination.setFingerprint(source.getFingerprint());
        destination.setExpires(source.getExpires());
        return destination;
    }

}

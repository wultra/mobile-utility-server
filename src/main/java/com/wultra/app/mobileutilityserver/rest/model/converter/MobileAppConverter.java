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

package com.wultra.app.mobileutilityserver.rest.model.converter;

import com.wultra.app.mobileutilityserver.database.model.CertificateEntity;
import com.wultra.app.mobileutilityserver.database.model.MobileAppEntity;
import com.wultra.app.mobileutilityserver.database.model.MobileDomainEntity;
import com.wultra.app.mobileutilityserver.rest.model.entity.Domain;
import com.wultra.app.mobileutilityserver.rest.model.entity.FullCertificateInfo;
import com.wultra.app.mobileutilityserver.rest.model.response.ApplicationDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converter class for mobile app and domain info.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Component
public class MobileAppConverter {

    private final CertificateConverter certificateConverter;

    @Autowired
    public MobileAppConverter(CertificateConverter certificateConverter) {
        this.certificateConverter = certificateConverter;
    }

    /**
     * Convert mobile app model to application detail response.
     * @param source Mobile app DB model.
     * @return Mobile app REST response.
     */
    public ApplicationDetailResponse convertMobileApp(MobileAppEntity source) {
        if (source == null) {
            return null;
        }
        final ApplicationDetailResponse destination = new ApplicationDetailResponse();
        destination.setName(source.getName());
        destination.setDisplayName(source.getDisplayName());
        destination.setPublicKey(source.getSigningPublicKey());
        for (MobileDomainEntity mobileDomainEntity : source.getDomains()) {
            final Domain domain = convertDomain(mobileDomainEntity);
            destination.getDomains().add(domain);
        }
        return destination;
    }

    public Domain convertDomain(MobileDomainEntity source) {
        if (source == null) {
            return null;
        }
        final Domain destination = new Domain();
        destination.setName(source.getDomain());
        for (CertificateEntity certificateEntity : source.getCertificates()) {
            final FullCertificateInfo certificateInfo = certificateConverter.convertFrom(certificateEntity);
            destination.getCertificates().add(certificateInfo);
        }
        return destination;
    }

}

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
import com.wultra.app.mobileutilityserver.rest.model.converter.MobileAppConverter;
import com.wultra.app.mobileutilityserver.rest.model.entity.DomainsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for operations related to domains.
 *
 * @author Pavel Sindelar, pavel.sindelar@wultra.com
 */
@Service
@RequiredArgsConstructor
public class MobileDomainService {

    private final MobileDomainRepository mobileDomainRepository;
    private final MobileAppConverter mobileAppConverter;

    @Transactional(readOnly = true)
    public DomainsConfig getDomainsConfig(final String applicationName) {
        final List<MobileDomainEntity> domains = mobileDomainRepository.findAllByAppName(applicationName);

        return new DomainsConfig(
                true,
                domains.stream().map(mobileAppConverter::convertDomain).toList()
        );
    }
}

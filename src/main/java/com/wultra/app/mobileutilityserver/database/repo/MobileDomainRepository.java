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

package com.wultra.app.mobileutilityserver.database.repo;

import com.wultra.app.mobileutilityserver.database.model.MobileDomainEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for mobile app domains.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Repository
public interface MobileDomainRepository extends CrudRepository<MobileDomainEntity, Long> {

    /**
     * Find first domain by domain name.
     * @param appName App name.
     * @param domain Domain name.
     * @return Entity by domain name.
     */
    MobileDomainEntity findFirstByAppNameAndDomain(String appName, String domain);

    /**
     * Delete domains by domain name.
     * @param appName App name.
     * @param domain Domain name.
     */
    void deleteByAppNameAndDomain(String appName, String domain);
}

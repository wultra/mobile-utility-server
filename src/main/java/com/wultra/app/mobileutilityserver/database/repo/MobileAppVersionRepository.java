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

import com.wultra.app.mobileutilityserver.database.model.MobileAppVersionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for {@link MobileAppVersionEntity}.
 *
 * @author Lubos Racansky, lubos.racansky@wultra.com
 */
public interface MobileAppVersionRepository extends CrudRepository<MobileAppVersionEntity, Long> {

    /**
     * Find application version by given application name, platform and major system version.
     *
     * @param applicationName Application name
     * @param platform Platform
     * @param majorOsVersion Major operation system version. For Apple e.g. 12.4.2 it is 12. For Android, it is API level e.g. 29.
     * @return found application version or empty
     */
    Optional<MobileAppVersionEntity> findFirstByApplicationNameAndPlatformAndMajorOsVersion(String applicationName, MobileAppVersionEntity.Platform platform, int majorOsVersion);

    /**
     * Find application version by given application name and platform.
     *
     * @param applicationName Application name
     * @param platform Platform
     * @return found application version or empty
     */
    Optional<MobileAppVersionEntity> findFirstByApplicationNameAndPlatform(String applicationName, MobileAppVersionEntity.Platform platform);

}

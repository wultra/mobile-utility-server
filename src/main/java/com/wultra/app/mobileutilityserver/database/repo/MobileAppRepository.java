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

package com.wultra.app.mobileutilityserver.database.repo;

import com.wultra.app.mobileutilityserver.database.model.MobileAppEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository class for accessing the mobile application metadata in the database.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
public interface MobileAppRepository extends CrudRepository<MobileAppEntity, Long> {

    /**
     * Checks if application by the application name exists.
     * @param name App name.
     * @return True in case the app exists, false otherwise.
     */
    boolean existsByName(String name);

    /**
     * Find the first application by the application name.
     * @param name App name.
     * @return Entity representing an application.
     */
    MobileAppEntity findFirstByName(String name);

}

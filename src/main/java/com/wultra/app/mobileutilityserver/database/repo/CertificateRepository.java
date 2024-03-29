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

import com.wultra.app.mobileutilityserver.database.model.CertificateEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for accessing the SSL certificates in the database.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Repository
public interface CertificateRepository extends CrudRepository<CertificateEntity, Long> {

    /**
     * Get the collection of SSL certificates for a given app.
     * @param appName App name.
     * @return List of SSL certificates.
     */
    @Query("SELECT s FROM CertificateEntity s WHERE s.domain.app.name = :appName")
    List<CertificateEntity> findAllByAppName(@Param("appName") String appName);

    @Query("SELECT s FROM CertificateEntity s WHERE s.domain.app.name = :appName AND s.domain.domain = :domain")
    List<CertificateEntity> findFirstByAppNameAndDomain(@Param("appName") String appName, @Param("domain") String domain);

    void deleteAllByExpiresBefore(Long expires);

}

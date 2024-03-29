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

package com.wultra.app.mobileutilityserver.rest.service;

import com.wultra.app.mobileutilityserver.database.model.CertificateEntity;
import com.wultra.app.mobileutilityserver.database.repo.CertificateRepository;
import com.wultra.app.mobileutilityserver.rest.model.converter.CertificateConverter;
import com.wultra.app.mobileutilityserver.rest.model.entity.CertificateFingerprint;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service acting as a DAO for SSL pinning fingerprints.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Service
public class CertificateFingerprintService {

    private final CertificateRepository repo;
    private final CertificateConverter converter;

    @Autowired
    public CertificateFingerprintService(CertificateRepository repo, CertificateConverter converter) {
        this.repo = repo;
        this.converter = converter;
    }

    /**
     * Return a collection of SSL pinning fingerprints. In case no fingerprints are present, the method
     * returns an empty collection.
     *
     * @param appName App name for which to return fingerprints.
     * @return Collection with SSL pinning fingerprints, possibly empty.
     */
    @Transactional
    public List<CertificateFingerprint> findCertificateFingerprintsByAppName(String appName) {
        final List<CertificateEntity> fingerprints = repo.findAllByAppName(appName);
        final List<CertificateFingerprint> result = new ArrayList<>(fingerprints.size());
        for (final CertificateEntity f: fingerprints) {
            final CertificateFingerprint fingerprint = converter.convertNamedCertificateFrom(f);
            if (fingerprint != null) {
                result.add(fingerprint);
            }
        }
        return result;
    }

}

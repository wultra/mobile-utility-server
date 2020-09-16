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

import com.wultra.app.mobileutilityserver.database.model.SslPinningFingerprintDbEntity;
import com.wultra.app.mobileutilityserver.database.repo.SslPinningFingerprintRepository;
import com.wultra.app.mobileutilityserver.rest.model.converter.SslPinningFingerprintConverter;
import com.wultra.app.mobileutilityserver.rest.model.entity.SslPinningFingerprint;
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
public class SslPinningDAO {

    private final SslPinningFingerprintRepository repo;
    private final SslPinningFingerprintConverter converter;

    @Autowired
    public SslPinningDAO(SslPinningFingerprintRepository repo, SslPinningFingerprintConverter converter) {
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
    public List<SslPinningFingerprint> findSslPinningFingerprintsByAppName(String appName) {
        final List<SslPinningFingerprintDbEntity> fingerprints = repo.findAllByAppName(appName);
        List<SslPinningFingerprint> result = new ArrayList<>();
        for (SslPinningFingerprintDbEntity f: fingerprints) {
            final SslPinningFingerprint fingerprint = converter.convertFrom(f);
            if (fingerprint != null) {
                result.add(fingerprint);
            }
        }
        return result;
    }

}

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
package com.wultra.app.mobileutilityserver.rest.model.response;

import com.wultra.app.mobileutilityserver.rest.model.entity.CertificateFingerprint;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Response object for the app init data resource.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Data
public class AppInitResponse {

    private final long timestamp = Instant.now().getEpochSecond();
    private final List<CertificateFingerprint> fingerprints;

    public AppInitResponse(List<CertificateFingerprint> fingerprints) {
        this.fingerprints = fingerprints;
    }
}

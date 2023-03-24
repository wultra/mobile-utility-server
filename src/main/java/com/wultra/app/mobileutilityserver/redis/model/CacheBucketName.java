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

package com.wultra.app.mobileutilityserver.redis.model;

/**
 * Name of the specific cache bucket (one bucket = one entity type).
 *
 * @author Petr Dvorak, petr@wultra.com
 */
public class CacheBucketName {

    /**
     * Bucket for certificate fingerprint cache.
     */
    public static final String CERTIFICATE_FINGERPRINTS = "CERTIFICATE_FINGERPRINTS";
    /**
     * Bucket for info about mobile apps.
     */
    public static final String MOBILE_APPS = "MOBILE_APPS";

    /**
     * Bucket for private key data.
     */
    public static final String PRIVATE_KEYS = "PRIVATE_KEYS";

}

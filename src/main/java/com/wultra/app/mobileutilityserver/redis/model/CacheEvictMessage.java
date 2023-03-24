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

import java.util.Objects;

/**
 * Data class representing cache eviction message.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
public class CacheEvictMessage {

    private final String type = "EVICT";
    private final CacheName cacheName;
    private final String appName;

    public CacheEvictMessage(CacheName cacheName, String appName) {
        this.cacheName = cacheName;
        this.appName = appName;
    }

    public String getType() {
        return type;
    }

    public CacheName getCacheName() {
        return cacheName;
    }

    public String getAppName() {
        return appName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheEvictMessage that)) return false;
        return cacheName == that.cacheName && appName.equals(that.appName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, cacheName, appName);
    }

    @Override
    public String toString() {
        return "CacheEvictMessage{" +
                "type='" + type + '\'' +
                ", cacheName=" + cacheName +
                ", appName='" + appName + '\'' +
                '}';
    }
}

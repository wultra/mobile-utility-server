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

package com.wultra.app.mobileutilityserver.rest.service;

import com.wultra.app.mobileutilityserver.redis.MessagePublisher;
import com.wultra.app.mobileutilityserver.redis.model.CacheBucketName;
import com.wultra.app.mobileutilityserver.redis.model.CacheEvictMessage;
import com.wultra.app.mobileutilityserver.redis.model.CacheName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * @author Petr Dvorak, petr@wultra.com
 */
@Service
public class CacheService {

    private final CacheManager cacheManager;
    private final MessagePublisher redisMessagePublisher;

    @Autowired
    public CacheService(CacheManager cacheManager, MessagePublisher redisMessagePublisher) {
        this.cacheManager = cacheManager;
        this.redisMessagePublisher = redisMessagePublisher;
    }

    /**
     * Notify nodes that mobile app related data should be evicted from node cache.
     * @param appName App name.
     */
    public void notifyEvictAppCache(String appName) {
        redisMessagePublisher.publish(new CacheEvictMessage(CacheName.MOBILE_APPS, appName));
    }

    /**
     * Notify nodes that certificate related data should be evicted from node cache.
     * @param appName App name.
     */
    public void notifyEvictCertificateCache(String appName) {
        redisMessagePublisher.publish(new CacheEvictMessage(CacheName.CERTIFICATE_FINGERPRINTS, appName));
    }

    /**
     * Evict caches for mobile apps and private keys for given app name.
     * @param appName App Name.
     */
    public void evictMobileAppAndPrivateKeyCache(String appName) {
        final Cache mobileAppsCache = cacheManager.getCache(CacheBucketName.MOBILE_APPS);
        if (mobileAppsCache != null) {
            mobileAppsCache.evict(appName);
        }
        final Cache privateKeyCache = cacheManager.getCache(CacheBucketName.PRIVATE_KEYS);
        if (privateKeyCache != null) {
            privateKeyCache.evict(appName);
        }
    }

    /**
     * Evict certificate fingerprint cache for a given app name.
     * @param appName App name.
     */
    public void evictCertificateFingerprintCache(String appName) {
        final Cache cache = cacheManager.getCache(CacheBucketName.CERTIFICATE_FINGERPRINTS);
        if (cache != null) {
            cache.evict(appName);
        }
    }

    /**
     * Invalidate values in the certificate cache.
     */
    public void invalidateCertificateCache() {
        final Cache cache = cacheManager.getCache(CacheBucketName.CERTIFICATE_FINGERPRINTS);
        if (cache != null) {
            cache.invalidate();
        }
    }

}

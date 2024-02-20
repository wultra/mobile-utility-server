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

package com.wultra.app.mobileutilityserver.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wultra.app.mobileutilityserver.redis.model.CacheEvictMessage;
import com.wultra.app.mobileutilityserver.rest.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Redis backed message subscriber.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Service
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    /**
     * Primary constructor accepting caching service and object mapper.
     * @param cacheService Cache service.
     * @param objectMapper Object mapper.
     */
    public RedisMessageSubscriber(CacheService cacheService, ObjectMapper objectMapper) {
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    /**
     * Callback method received whenever a new message arrives.
     * @param message Message.
     * @param pattern Pattern.
     */
    public void onMessage(Message message, byte[] pattern) {
        try {
            final CacheEvictMessage msg = objectMapper.readValue(message.getBody(), new TypeReference<>() {});

            logger.info("Received message: {}", msg.toString());

            switch (msg.getCacheName()) {
                case MOBILE_APPS -> cacheService.evictMobileAppAndPrivateKeyCache(msg.getAppName());
                case CERTIFICATE_FINGERPRINTS -> cacheService.evictCertificateFingerprintCache(msg.getAppName());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
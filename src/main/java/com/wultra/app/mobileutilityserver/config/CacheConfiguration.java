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

package com.wultra.app.mobileutilityserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wultra.app.mobileutilityserver.redis.RedisMessageSubscriber;
import com.wultra.app.mobileutilityserver.rest.service.CacheService;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Configuration for caching.
 *
 * @author Petr Dvorak, petr@wultra.com
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    public ChannelTopic topic() {
        return new ChannelTopic("channel-in-memory-cache-eviction");
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisConfiguration redisConfiguration) {
        return new LettuceConnectionFactory(
                redisConfiguration.getRedisHost(),
                redisConfiguration.getRedisPort()
        );
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConfiguration redisConfiguration) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory(redisConfiguration));
        template.setDefaultSerializer(redisSerializer());
        return template;
    }

    @Bean
    public MessageListener messageListener(CacheService cacheService, ObjectMapper objectMapper) {
        return new RedisMessageSubscriber(cacheService, objectMapper);
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(CacheService cacheService, ObjectMapper objectMapper) {
        return new MessageListenerAdapter(messageListener(cacheService, objectMapper));
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConfiguration redisConfiguration, CacheService cacheService, ObjectMapper objectMapper) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory(redisConfiguration));
        container.addMessageListener(messageListener(cacheService, objectMapper), topic());
        return container;
    }

}

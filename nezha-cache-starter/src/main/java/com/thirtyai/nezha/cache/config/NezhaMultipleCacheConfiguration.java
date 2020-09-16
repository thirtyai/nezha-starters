/*
 * Copyright (c) 2019-2020 kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.cache.config;

import com.thirtyai.nezha.cache.MultipleCacheManager;
import com.thirtyai.nezha.cache.message.CacheKeyMessagePubSub;
import com.thirtyai.nezha.cache.props.NezhaMultipleCacheProperties;
import com.thirtyai.nezha.cache.refresh.CacheKeyValueRefreshSupport;
import com.thirtyai.nezha.cache.refresh.ICacheKeyValueRefreshSupport;
import com.thirtyai.nezha.redis.operator.RedisOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer;
import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizers;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * nezha multiple cache configuration
 *
 * @author kyleju
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(NezhaMultipleCacheProperties.class)
@RequiredArgsConstructor
public class NezhaMultipleCacheConfiguration {
	private final RedisOperator redisOperator;
	private final ObjectProvider<List<CacheManagerCustomizer<?>>> customers;
	private final ThreadPoolTaskExecutor taskExecutor;
	private final NezhaMultipleCacheProperties nezhaMultipleCacheProperties;
	private final ObjectProvider<RedisConnectionFactory> redisConnectionFactoryObjectProvider;

	/**
	 * cache key message pub sub, for Clear, Evict Caches
	 *
	 * @return bean {@link CacheKeyMessagePubSub}
	 */
	@Bean
	public CacheKeyMessagePubSub cacheKeyMessagePubSub() {
		return new CacheKeyMessagePubSub(redisOperator);
	}

	/**
	 * cache manager customizers
	 *
	 * @return bean {@link CacheManagerCustomizers}
	 */
	@Bean
	public CacheManagerCustomizers cacheManagerCustomizers() {
		return new CacheManagerCustomizers(customers.getIfAvailable());
	}

	/**
	 * multiple cache manager with Caffeine/Redis/CaffeineRedis/None caches
	 *
	 * @return bean {@link MultipleCacheManager}
	 */
	@Bean
	@Primary
	public MultipleCacheManager cacheManager() {
		CacheKeyMessagePubSub cacheKeyMessagePubSub = cacheKeyMessagePubSub();
		MultipleCacheManager multipleCacheManager = new MultipleCacheManager(nezhaMultipleCacheProperties, cacheKeyMessagePubSub, redisConnectionFactoryObjectProvider, redisOperator);
		cacheKeyMessagePubSub.setMultipleCacheManager(multipleCacheManager);
		cacheManagerCustomizers().customize(multipleCacheManager);
		return multipleCacheManager;
	}

	@Bean
	public ICacheKeyValueRefreshSupport cacheKeyValueRefreshSupport() {
		return new CacheKeyValueRefreshSupport(cacheManager(), taskExecutor);
	}

	/**
	 * log error
	 */
	@Bean
	public CacheErrorHandler errorHandler() {
		return new LoggingCacheErrorHandler();
	}

	@Slf4j
	static class LoggingCacheErrorHandler extends SimpleCacheErrorHandler {
		@Override
		public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
			log.error("cacheName:{} ,cacheKey: {} , exception {}", cache.getName(), key, exception);
			super.handleCacheGetError(exception, cache, key);
		}

		@Override
		public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
			log.error("cacheName:{} ,cacheKey: {} , exception {}", cache.getName(), key, exception);
			super.handleCachePutError(exception, cache, key, value);
		}

		@Override
		public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
			log.error("cacheName:{} ,cacheKey: {} , exception {}", cache.getName(), key, exception);
			super.handleCacheEvictError(exception, cache, key);
		}

		@Override
		public void handleCacheClearError(RuntimeException exception, Cache cache) {
			log.error(String.format("cacheName:{} , exception {}", cache.getName()), exception);
			super.handleCacheClearError(exception, cache);
		}
	}
}

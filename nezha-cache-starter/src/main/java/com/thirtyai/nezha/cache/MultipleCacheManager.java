/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.cache;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.thirtyai.nezha.cache.message.CacheKeyMessagePubSub;
import com.thirtyai.nezha.cache.props.NezhaMultipleCacheProperties;
import com.thirtyai.nezha.cache.redis.RedisCache;
import com.thirtyai.nezha.redis.operator.RedisOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Multiple cache manager
 *
 * @author kyleju
 */
@RequiredArgsConstructor
@Slf4j
public class MultipleCacheManager implements CacheManager, InitializingBean {
	private final static String CAFFEINE_END_STRING = "#c";
	private final static String REDIS_END_STRING = "#r";
	private final static String CAFFEINE_REDIS_END_STRING = "#cr";
	private final static int TWO = 2;
	private final static int THREE = 3;
	private final static int FOUR = 4;

	private final NezhaMultipleCacheProperties multipleCacheProperties;
	private final CacheKeyMessagePubSub cacheKeyMessagePubSub;
	private final ObjectProvider<RedisConnectionFactory> redisConnectionFactoryObjectProvider;
	private final RedisOperator redisOperator;

	private final Map<String, MultipleCache> caches = MapUtil.newConcurrentHashMap();

	@Override
	public void afterPropertiesSet() {
		if (multipleCacheProperties.getCaffeine() != null) {
			multipleCacheProperties.getCaffeine().forEach(item -> {
				try {
					if (item.validateSelf()) {
						if (!caches.containsKey(item.getName().toLowerCase())) {
							MultipleCache multipleCache = createMultipleCache(item, MultipleCacheType.Caffeine, null);
							if (multipleCache != null) {
								caches.put(item.getName().toLowerCase(), multipleCache);
							}
						}
					}
				} catch (Exception exception) {
					log.error("{}[{}]", ExceptionUtil.getMessage(exception), item.getName());
				}
			});
		}

		redisConnectionFactoryObjectProvider.ifAvailable(redisConnectionFactory -> {
			if (multipleCacheProperties.getRedis() != null) {
				multipleCacheProperties.getRedis().forEach(item -> {
					try {
						if (item.validateSelf()) {
							if (!caches.containsKey(item.getName().toLowerCase())) {
								MultipleCache multipleCache = createMultipleCache(item, MultipleCacheType.Caffeine, redisConnectionFactory);
								if (multipleCache != null) {
									caches.put(item.getName().toLowerCase(), multipleCache);
								}
							}
						}
					} catch (Exception exception) {
						log.error("{}[{}]", ExceptionUtil.getMessage(exception), item.getName());
					}
				});
			}

			if (multipleCacheProperties.getCaffeineRedis() != null) {
				multipleCacheProperties.getCaffeineRedis().forEach(item -> {
					try {
						if (item.validateSelf()) {
							if (!caches.containsKey(item.getName().toLowerCase())) {
								MultipleCache multipleCache = createMultipleCache(item, MultipleCacheType.CaffeineRedis, redisConnectionFactory);
								if (multipleCache != null) {
									caches.put(item.getName().toLowerCase(), multipleCache);
								}
							}
						}
					} catch (Exception exception) {
						log.error("{}[{}]", ExceptionUtil.getMessage(exception), item.getName());
					}
				});
			}
		});
	}

	/**
	 * create multiple cache
	 *
	 * @param item              cache item {@link com.thirtyai.nezha.cache.props.NezhaMultipleCacheProperties.CacheItem}
	 * @param multipleCacheType multiple cache type {@link MultipleCacheType}
	 * @return value {@link MultipleCache}
	 */
	@Nullable
	private MultipleCache createMultipleCache(NezhaMultipleCacheProperties.CacheItem item, MultipleCacheType multipleCacheType, RedisConnectionFactory connectionFactory) {
		switch (multipleCacheType) {
			case Caffeine:
				return new MultipleCache(item, createCaffeineCache(item), null, cacheKeyMessagePubSub);
			case Redis:
				return new MultipleCache(item, null, createRedisCache(item, connectionFactory), cacheKeyMessagePubSub);
			case CaffeineRedis:
				return new MultipleCache(item, createCaffeineCache(item), createRedisCache(item, connectionFactory), cacheKeyMessagePubSub);
			default:
				return null;
		}
	}

	/**
	 * create redis cache
	 *
	 * @param item              cache item {@link com.thirtyai.nezha.cache.props.NezhaMultipleCacheProperties.CacheItem}
	 * @param connectionFactory connection factory {@link RedisConnectionFactory}
	 * @return value {@link RedisCache}
	 */
	private RedisCache createRedisCache(NezhaMultipleCacheProperties.CacheItem item, RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration redisCacheConfiguration = determineConfiguration(item.getTtlSeconds());
		if (redisCacheConfiguration == null) {
			return null;
		}
		return new RedisCache(item.getName(), RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory), redisCacheConfiguration);
	}

	/**
	 * create caffeine cache
	 *
	 * @param item {@link com.thirtyai.nezha.cache.props.NezhaMultipleCacheProperties.CacheItem}
	 * @return value {@link CaffeineCache}
	 */
	private CaffeineCache createCaffeineCache(NezhaMultipleCacheProperties.CacheItem item) {
		Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
		caffeine.expireAfterWrite(item.getTtlSeconds(), TimeUnit.SECONDS);
		if (multipleCacheProperties.getCacheMaxSize() > 0) {
			caffeine.maximumSize(multipleCacheProperties.getCacheMaxSize());
		}
		return new CaffeineCache(item.getName(), caffeine.build(), multipleCacheProperties.isAllowNullValues());
	}

	/**
	 * get cache
	 * if cache name didn't init, will be create.
	 * name format: cacheSortName_ttl_rts_rss#[R|C|CR]
	 *
	 * @param name cache name, name not blank.
	 * @return value
	 */
	@Override
	public MultipleCache getCache(@NonNull String name) {
		if (caches.containsKey(name.toLowerCase())) {
			return caches.get(name.toLowerCase());
		}

		MultipleCacheType multipleCacheType = MultipleCacheType.None;

		if (name.toLowerCase().endsWith(REDIS_END_STRING)) {
			multipleCacheType = MultipleCacheType.Redis;
		}
		if (name.toLowerCase().endsWith(CAFFEINE_END_STRING)) {
			multipleCacheType = MultipleCacheType.Caffeine;
		}
		if (name.toLowerCase().endsWith(CAFFEINE_REDIS_END_STRING)) {
			multipleCacheType = MultipleCacheType.CaffeineRedis;
		}

		if (!multipleCacheType.equals(MultipleCacheType.None)) {
			String[] nameParts = name.substring(0, name.lastIndexOf("#")).split(StrUtil.UNDERLINE);
			NezhaMultipleCacheProperties.CacheItem cacheItem = new NezhaMultipleCacheProperties.CacheItem();
			cacheItem.setName(name);
			if (nameParts.length >= TWO) {
				cacheItem.setTtlSeconds(Convert.toInt(nameParts[1], cacheItem.getTtlSeconds()));
			}
			if (nameParts.length >= THREE) {
				cacheItem.setRefreshTriggerSeconds(Convert.toInt(nameParts[2], cacheItem.getRefreshTriggerSeconds()));
			}
			if (nameParts.length >= FOUR) {
				cacheItem.setRefresherSleepSeconds(Convert.toInt(nameParts[3], cacheItem.getRefresherSleepSeconds()));
			}
			try {
				if (cacheItem.validateSelf()) {
					RedisConnectionFactory redisConnectionFactory = redisConnectionFactoryObjectProvider.getIfAvailable();
					MultipleCache multipleCache;
					if (redisConnectionFactory == null || multipleCacheType.equals(MultipleCacheType.Caffeine)) {
						multipleCache = createMultipleCache(cacheItem, multipleCacheType, null);
					} else {
						multipleCache = createMultipleCache(cacheItem, multipleCacheType, redisConnectionFactory);
					}
					if (multipleCache != null) {
						caches.put(cacheItem.getName().toLowerCase(), multipleCache);
						return multipleCache;
					}
				}
			} catch (Exception exception) {
				log.error("{}[{}]", ExceptionUtil.getMessage(exception), name);
			}
		}
		log.warn("no {} cache", name);
		return null;
	}

	@NonNull
	@Override
	public Collection<String> getCacheNames() {
		return caches.keySet();
	}

	/**
	 * determine configuration
	 *
	 * @return value
	 */
	private RedisCacheConfiguration determineConfiguration(int ttlSeconds) {
		if (ttlSeconds <= 0) {
			return null;
		}
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(this.redisOperator.getRedisTemplate().getValueSerializer()));
		config = config.entryTtl(Duration.ofSeconds(ttlSeconds));
		config = config.disableCachingNullValues();
		return config;
	}
}

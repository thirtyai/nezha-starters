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
package com.thirtyai.nezha.cache;

import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.cache.message.CacheKeyMessagePubSub;
import com.thirtyai.nezha.cache.message.CacheKeyOperateMessage;
import com.thirtyai.nezha.cache.message.OperateMessageType;
import com.thirtyai.nezha.cache.props.NezhaMultipleCacheProperties;
import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;

/**
 * Multiple cache
 *
 * @author kyleju
 */
public class MultipleCache implements Cache {
	@Getter
	private MultipleCacheType multipleCacheType = MultipleCacheType.None;

	/**
	 * cache name
	 */
	@Getter
	private final NezhaMultipleCacheProperties.CacheItem cacheItem;
	/**
	 * caffeine cache
	 */
	@Getter
	private final Cache caffeine;
	/**
	 * redis cache
	 */
	@Getter
	private final Cache redis;

	/**
	 * cache key message pub sub
	 */
	@Getter
	private final CacheKeyMessagePubSub cacheKeyMessagePubSub;

	public MultipleCache(NezhaMultipleCacheProperties.CacheItem cacheItem, Cache caffeine, Cache redis, CacheKeyMessagePubSub cacheKeyMessagePubSub) {
		this.cacheItem = cacheItem;
		this.caffeine = caffeine;
		this.redis = redis;
		this.cacheKeyMessagePubSub = cacheKeyMessagePubSub;

		if (this.caffeine != null && this.redis != null) {
			this.multipleCacheType = MultipleCacheType.CaffeineRedis;
		} else if (this.caffeine == null && this.redis != null) {
			this.multipleCacheType = MultipleCacheType.Redis;
		} else if (this.caffeine != null) {
			this.multipleCacheType = MultipleCacheType.Caffeine;
		}
	}

	@NonNull
	@Override
	public String getName() {
		return this.cacheItem.getName();
	}

	@NonNull
	@Override
	public Object getNativeCache() {
		return this;
	}

	@Override
	public ValueWrapper get(@NonNull Object key) {
		if(StrUtil.isBlank(key.toString())){
			return nullValueWrapper();
		}
		switch (multipleCacheType) {
			case Caffeine:
				return caffeine.get(key);
			case Redis:
				return redis.get(key);
			case CaffeineRedis:
				ValueWrapper valueWrapper = caffeine.get(key);
				if (valueWrapper == null || valueWrapper.get() == null) {
					valueWrapper = redis.get(key);
					if (valueWrapper != null && valueWrapper.get() != null) {
						caffeine.put(key, valueWrapper.get());
					}
				}
				return valueWrapper == null ? nullValueWrapper() : valueWrapper;
			default:
				return nullValueWrapper();
		}
	}

	@Override
	public <T> T get(@NonNull Object key, Class<T> type) {
		if(StrUtil.isBlank(key.toString())){
			return null;
		}
		switch (multipleCacheType) {
			case Caffeine:
				return caffeine.get(key, type);
			case Redis:
				return redis.get(key, type);
			case CaffeineRedis:
				T object = caffeine.get(key, type);
				if (object == null) {
					object = redis.get(key, type);
					if (object != null) {
						caffeine.put(key, object);
					}
				}
				return object;
			default:
				return null;
		}
	}

	@Override
	public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
		if(StrUtil.isBlank(key.toString())){
			return null;
		}
		switch (multipleCacheType) {
			case Caffeine:
				return caffeine.get(key, valueLoader);
			case Redis:
				return redis.get(key, valueLoader);
			case CaffeineRedis:
				T object = caffeine.get(key, valueLoader);
				if (object == null) {
					object = redis.get(key, valueLoader);
					if (object != null) {
						this.caffeine.put(key, object);
					}
				}
				return object;
			default:
				return null;
		}
	}

	@Override
	public void put(@NonNull Object key, Object value) {
		if(StrUtil.isNotBlank(key.toString())) {
			switch (multipleCacheType) {
				case Caffeine:
					caffeine.put(key, value);
					break;
				case Redis:
					redis.put(key, value);
					break;
				case CaffeineRedis:
					redis.put(key, value);
					evictCaffeineCache(key);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public ValueWrapper putIfAbsent(@NonNull Object key, Object value) {
		if(StrUtil.isBlank(key.toString())){
			return nullValueWrapper();
		}
		switch (multipleCacheType) {
			case Caffeine:
				return caffeine.putIfAbsent(key, value);
			case Redis:
				return redis.putIfAbsent(key, value);
			case CaffeineRedis:
				ValueWrapper valueWrapper = redis.putIfAbsent(key, value);
				evictCaffeineCache(key);
				return valueWrapper;
			default:
				return nullValueWrapper();
		}

	}

	@Override
	public void evict(@NonNull Object key) {
		if(StrUtil.isNotBlank(key.toString())) {
			switch (multipleCacheType) {
				case Caffeine:
					caffeine.evict(key);
					break;
				case Redis:
					redis.evict(key);
					break;
				case CaffeineRedis:
					redis.evict(key);
					evictCaffeineCache(key);
					break;
				default:
					break;
			}
		}
	}

	@Override
	public boolean evictIfPresent(@NonNull Object key) {
		if(StrUtil.isBlank(key.toString())) {
			return false;
		}
		switch (multipleCacheType) {
			case Caffeine:
				return caffeine.evictIfPresent(key);
			case Redis:
				return redis.evictIfPresent(key);
			case CaffeineRedis:
				boolean value = redis.evictIfPresent(key);
				if (value) {
					evictCaffeineCache(key);
				}
				return value;
			default:
				return true;
		}
	}

	@Override
	public void clear() {
		switch (multipleCacheType) {
			case Caffeine:
				caffeine.clear();
				break;
			case Redis:
				redis.clear();
				break;
			case CaffeineRedis:
				redis.clear();
				clearCaffeineCache();
				break;
			default:
				break;
		}
	}

	@Override
	public boolean invalidate() {
		if (caffeine != null) {
			caffeine.invalidate();
		}
		if (redis != null) {
			redis.invalidate();
		}
		return false;
	}

	private void evictCaffeineCache(Object key) {
		cacheKeyMessagePubSub.publish(new CacheKeyOperateMessage(this.getName(), key.toString(), OperateMessageType.Evict));
	}

	private void clearCaffeineCache() {
		cacheKeyMessagePubSub.publish(new CacheKeyOperateMessage(this.getName(), null, OperateMessageType.Clear));
	}

	protected Cache.ValueWrapper nullValueWrapper() {
		return new SimpleValueWrapper(null);
	}
}

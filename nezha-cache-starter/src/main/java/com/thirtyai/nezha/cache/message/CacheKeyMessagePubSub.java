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
package com.thirtyai.nezha.cache.message;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.cache.MultipleCache;
import com.thirtyai.nezha.cache.MultipleCacheManager;
import com.thirtyai.nezha.redis.operator.RedisOperator;
import com.thirtyai.nezha.redis.pubsub.AbstractPubSub;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * cache message pub sub
 *
 * @author kyleju
 */
@Slf4j
public class CacheKeyMessagePubSub extends AbstractPubSub<CacheKeyOperateMessage> {
	private static final String CACHE_KEY_MESSAGE_TOPIC = "nezha_cache_key_message_pub_sub";
	@Setter
	@Getter
	private MultipleCacheManager multipleCacheManager;

	private CacheKeyMessagePubSub(String topic, RedisOperator redisOperator) {
		super(topic, redisOperator);
	}

	public CacheKeyMessagePubSub(RedisOperator redisOperator) {
		this(CACHE_KEY_MESSAGE_TOPIC, redisOperator);
	}

	@Override
	public void onMessage(CacheKeyOperateMessage message) {
		if (message != null && StrUtil.isNotBlank(message.getCacheName()) && multipleCacheManager != null) {

			MultipleCache multipleCache = multipleCacheManager.getCache(message.getCacheName());
			if (multipleCache != null) {
				if (message.getOperateMessageType().equals(OperateMessageType.Clear)) {
					if (multipleCache.getCaffeine() != null) {
						multipleCache.getCaffeine().clear();
					}
				}

				if (message.getOperateMessageType().equals(OperateMessageType.Evict)) {
					if (multipleCache.getCaffeine() != null) {
						multipleCache.getCaffeine().evict(message.getKey());
					}
				}
			}
		}
	}

	@Override
	public void onError(Exception exception) {
		log.error(ExceptionUtil.getMessage(exception));
	}
}

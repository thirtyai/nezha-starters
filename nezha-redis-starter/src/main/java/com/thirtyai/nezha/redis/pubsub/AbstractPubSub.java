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
package com.thirtyai.nezha.redis.pubsub;

import com.thirtyai.nezha.common.util.JsonUtil;
import com.thirtyai.nezha.redis.operator.RedisOperator;
import lombok.Getter;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * abstract pub sub
 *
 * @param <T>
 * @author kyleju
 */
@SuppressWarnings("unchecked")
public abstract class AbstractPubSub<T> implements MessageListener {
	@Getter
	private final ChannelTopic channelTopic;
	private final RedisOperator redisOperator;

	public AbstractPubSub(@NonNull ChannelTopic channelTopic, @NonNull RedisOperator redisOperator) {
		this.channelTopic = channelTopic;
		this.redisOperator = redisOperator;
	}

	public AbstractPubSub(@NonNull String topic, @NonNull RedisOperator redisOperator) {
		this(ChannelTopic.of(topic), redisOperator);
	}

	public void publish(@NonNull T message) {
		this.redisOperator.publish(this.channelTopic, message);
	}

	@Override
	public void onMessage(Message message, byte[] pattern) {
		if (message != null) {
			Type genericSuperclass = this.getClass().getGenericSuperclass();
			ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			Class<T> tClass = (Class<T>) actualTypeArguments[0];
			try {
				this.onMessage(JsonUtil.parse(JsonUtil.parse(message.toString(), String.class), tClass));
			} catch (Exception e) {
				this.onError(e);
			}
		}
	}

	/**
	 * on message
	 *
	 * @param message message
	 */
	public abstract void onMessage(@Nullable T message);

	/**
	 * on error
	 *
	 * @param exception exception {@link Exception}
	 */
	public abstract void onError(Exception exception);
}

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
package com.thirtyai.nezha.redis.config;

import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.redis.pubsub.AbstractPubSub;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;

/**
 * redis pub sub configruation
 *
 * @author kyleju
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "nezha.starters", name = "redis", havingValue = "true")
public class RedisPubSubConfiguration {
	private final RedisConnectionFactory redisConnectionFactory;
	private final List<AbstractPubSub> pubSubMessageList;
	@Qualifier(NezhaConstant.DEFAULT_TASK_EXECUTOR)
	private final TaskExecutor taskExecutor;

	/**
	 * redis pub sub message container
	 *
	 * @return value {@link RedisMessageListenerContainer}
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer() {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);
		container.setTaskExecutor(taskExecutor);

		if (pubSubMessageList != null) {
			pubSubMessageList.forEach(item -> {
				container.addMessageListener(item, item.getChannelTopic());
			});
		}
		return container;
	}
}

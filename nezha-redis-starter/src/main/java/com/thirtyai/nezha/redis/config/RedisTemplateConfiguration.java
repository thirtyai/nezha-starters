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
package com.thirtyai.nezha.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thirtyai.nezha.redis.operator.RedisOperator;
import com.thirtyai.nezha.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis template configuration
 *
 * @author kyleju
 */

@Configuration
@AutoConfigureAfter({RedisAutoConfiguration.class})
@ConditionalOnProperty(prefix = "nezha.starters", name = "redis", havingValue = "true")
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class RedisTemplateConfiguration implements InitializingBean {
	private final RedisConnectionFactory redisConnectionFactory;
	private ObjectMapper objectMapper;
	private final ObjectProvider<ObjectMapper> objectMapperProvider;

	/**
	 * redis value serializer
	 *
	 * @return value {@link RedisSerializer}
	 */
	@Bean
	public RedisSerializer<Object> redisSerializer() {
		return getJackson2JsonRedisSerializer();
	}

	/**
	 * redis template
	 *
	 * @param redisSerializer serializer
	 * @return redis template
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisSerializer<Object> redisSerializer) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
		redisTemplate.setKeySerializer(stringRedisSerializer);
		redisTemplate.setHashKeySerializer(stringRedisSerializer);
		redisTemplate.setValueSerializer(redisSerializer);
		redisTemplate.setHashValueSerializer(redisSerializer);
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		return redisTemplate;
	}

	/**
	 * redis operator
	 *
	 * @param redisTemplate redis template
	 * @return value {@link RedisOperator}
	 */
	@Bean("redisOperator")
	public RedisOperator redisOperator(RedisTemplate<String, Object> redisTemplate) {
		return new RedisOperator(redisTemplate);
	}

	@Override
	public void afterPropertiesSet() {
		objectMapper = JsonUtil.setJsonObjectMapperParams(objectMapperProvider.getIfAvailable());
	}

	private Jackson2JsonRedisSerializer getJackson2JsonRedisSerializer() {
		Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		return jackson2JsonRedisSerializer;
	}
}

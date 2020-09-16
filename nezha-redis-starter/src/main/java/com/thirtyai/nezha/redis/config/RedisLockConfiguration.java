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

import com.thirtyai.nezha.redis.lock.RedisLockService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author kyleju
 */
@Configuration
@AutoConfigureAfter(RedisTemplateConfiguration.class)
@Import(RedisTemplateConfiguration.class)
@ConditionalOnProperty(prefix = "nezha.starters", name = "redis", havingValue = "true")
class RedisLockConfiguration {

	@Bean("redisLock")
	public RedisLockService redisLock(RedisTemplate<String, Object> redisTemplate) {
		return new RedisLockService(redisTemplate);
	}
}

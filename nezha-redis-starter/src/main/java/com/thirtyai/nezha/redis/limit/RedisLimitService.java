/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.redis.limit;

import cn.hutool.core.convert.Convert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class RedisLimitService implements IRedisLimit {

	private final RedisTemplate<String, Object> redisTemplate;

	private final DefaultRedisScript<Long> Limit_LUA_SCRIPT = new DefaultRedisScript<>(
		"-- lua 下标从 1 开始\n" +
			"-- 限流 \n" +
			"local key = KEYS[1]\n" +
			"-- 限流大小\n" +
			"local max = tonumber(ARGV[1])\n" +
			"-- 超时时间\n" +
			"local ttl = tonumber(ARGV[2])\n" +
			"local now = tonumber(ARGV[3])\n" +
			"local expired = now - (ttl * 1000)\n" +
			"\n" +
			"redis.call('zremrangebyscore', key, 0, expired)\n" +
			"-- 获取当前流量大小\n" +
			"local currentLimit = tonumber(redis.call('zcard', key))\n" +
			"\n" +
			"local nextLimit = currentLimit + 1\n" +
			"if nextLimit > max then\n" +
			"    return 0;\n" +
			"else\n" +
			"    redis.call(\"zadd\", key, now, now)\n" +
			"    redis.call(\"pexpire\", key, ttl)\n" +
			"    return nextLimit\n" +
			"end\n"
		, Long.class
	);

	@Override
	public Long limit(String key, long max, long ttl, TimeUnit timeUnit)  {
		// millis time
		long now = System.currentTimeMillis();
		long ttlMillis = timeUnit.toMillis(ttl);

		try {
			RedisCallback<Long> callback = (connection) -> connection.eval(this.Limit_LUA_SCRIPT.getScriptAsString().getBytes(StandardCharsets.UTF_8), ReturnType.fromJavaType(Limit_LUA_SCRIPT.getResultType()), 1, key.getBytes(StandardCharsets.UTF_8), (max + "").getBytes(StandardCharsets.UTF_8), (ttlMillis + "").getBytes(StandardCharsets.UTF_8), (now + "").getBytes(StandardCharsets.UTF_8));
			return Convert.toLong(redisTemplate.execute(callback));
		} catch (Exception e) {
			log.error("redis lock has an error.", e);
		}

		return 0L;
	}
}

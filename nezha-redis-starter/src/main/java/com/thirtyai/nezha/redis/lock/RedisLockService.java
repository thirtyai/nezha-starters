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
package com.thirtyai.nezha.redis.lock;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * redis lock service
 *
 * @author kyleju
 */
@Slf4j
@RequiredArgsConstructor
public class RedisLockService implements IRedisLock {

	private final RedisTemplate<String, Object> redisTemplate;

	private final ThreadLocal<String> lockFlag = new ThreadLocal<>();

	/**
	 * lock lua script
	 */
	private final static DefaultRedisScript<Long> LOCK_LUA_SCRIPT = new DefaultRedisScript<>(
		"if redis.call(\"setnx\", KEYS[1], ARGV[1]) == 1 then return redis.call(\"pexpire\", KEYS[1], ARGV[2]) else return 0 end"
		, Long.class
	);

	/**
	 * release lock script
	 */
	private final static DefaultRedisScript<Long> UNLOCK_LUA_SCRIPT = new DefaultRedisScript<>(
		"if redis.call(\"get\",KEYS[1]) == ARGV[1] then return redis.call(\"del\",KEYS[1]) else return -1 end"
		, Long.class
	);

	@Override
	public boolean lock(String key, long expireMillis, int retryTimes, long sleepMillis) {
		boolean result = setRedis(key, expireMillis);
		while ((!result) && retryTimes-- > 0) {
			try {
				log.info("lock failed, retrying " + retryTimes + "'s");
				Thread.sleep(sleepMillis);
			} catch (InterruptedException e) {
				return false;
			}
			result = setRedis(key, expireMillis);
		}
		return result;
	}

	private boolean setRedis(final String key, final long expireMillis) {
		try {
			RedisCallback<Boolean> callback = (connection) -> {
				String uuid = UUID.randomUUID().toString();
				lockFlag.set(uuid);
				return connection.eval(LOCK_LUA_SCRIPT.getScriptAsString().getBytes(StandardCharsets.UTF_8), ReturnType.BOOLEAN, 1, key.getBytes(StandardCharsets.UTF_8), uuid.getBytes(StandardCharsets.UTF_8), Convert.toStr(expireMillis).getBytes(StandardCharsets.UTF_8));
			};
			return redisTemplate.execute(callback);
		} catch (Exception e) {
			log.error("redis lock has an error.", e);
		}
		return false;

	}

	@Override
	public boolean releaseLock(String key) {
		try {
			RedisCallback<Boolean> callback = (connection) -> {
				String value = lockFlag.get();
				return connection.eval(UNLOCK_LUA_SCRIPT.getScriptAsString().getBytes(), ReturnType.BOOLEAN, 1, key.getBytes(Charset.forName("UTF-8")), value.getBytes(Charset.forName("UTF-8")));
			};
			return redisTemplate.execute(callback);
		} catch (Exception e) {
			log.error("release lock has an exception", e);
		} finally {
			lockFlag.remove();
		}
		return false;
	}
}

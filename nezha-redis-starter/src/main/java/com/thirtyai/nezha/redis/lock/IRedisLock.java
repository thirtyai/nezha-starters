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

/**
 * @author kyleju
 */
public interface IRedisLock {
	long TIMEOUT_MILLIS = 2000;

	int RETRY_TIMES = 5;

	long SLEEP_MILLIS = 500;

	/**
	 * lock key
	 *
	 * @param key
	 * @return
	 */
	default boolean lock(String key) {
		return lock(key, TIMEOUT_MILLIS, RETRY_TIMES, SLEEP_MILLIS);
	}

	/**
	 * lock key retrytimes
	 *
	 * @param key
	 * @param retryTimes
	 * @return
	 */
	default boolean lock(String key, int retryTimes) {
		return lock(key, TIMEOUT_MILLIS, retryTimes, SLEEP_MILLIS);
	}

	/**
	 * lock key retryTimes sleepMillis
	 *
	 * @param key
	 * @param retryTimes
	 * @param sleepMillis
	 * @return
	 */
	default boolean lock(String key, int retryTimes, long sleepMillis) {
		return lock(key, TIMEOUT_MILLIS, retryTimes, sleepMillis);
	}

	/**
	 * lock
	 *
	 * @param key
	 * @param expireMillis
	 * @return
	 */
	default boolean lock(String key, long expireMillis) {
		return lock(key, expireMillis, RETRY_TIMES, SLEEP_MILLIS);
	}

	/**
	 * lock
	 * key expire retryTimes
	 *
	 * @param key key
	 * @param expireMillis expire
	 * @param retryTimes
	 * @return
	 */
	default boolean lock(String key, long expireMillis, int retryTimes) {
		return lock(key, expireMillis, retryTimes, SLEEP_MILLIS);
	}

	/**
	 * lock
	 *
	 * @param key         key
	 * @param expireMillis      expire
	 * @param retryTimes  retry times
	 * @param sleepMillis sleep millis
	 * @return
	 */
	boolean lock(String key, long expireMillis, int retryTimes, long sleepMillis);

	boolean releaseLock(String key);

}

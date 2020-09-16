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

import cn.hutool.core.util.StrUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * redis Lock
 *
 * @author kyleju
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {
	/**
	 * @return key
	 */
	String key();

	/**
	 * for key + params 's cache, support sePL
	 *
	 * @return value
	 */
	String params() default StrUtil.EMPTY;

	/**
	 * lock expire time
	 *
	 * @return value
	 */
	long expireMills();

	/**
	 * retry times
	 *
	 * @return
	 */
	int retryTimes() default IRedisLock.RETRY_TIMES;

	/**
	 * set redis key value after，sleep time（cluster sync，standalone set zero）
	 *
	 * @return value
	 */
	long sleepMills() default IRedisLock.SLEEP_MILLIS;


}

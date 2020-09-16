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
package com.thirtyai.nezha.redis.limit;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Rate limit
 *
 * @author kyleju
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RateLimit {

	/**
	 * limit key
	 *
	 * @return key
	 */
	String key();

	/**
	 * limit param, support SpEL
	 *
	 * @return param
	 */
	String params() default "";

	/**
	 * max request，default: 500
	 *
	 * @return request count
	 */
	long max() default 500L;

	/**
	 * window size，default: 10
	 *
	 * @return ttl
	 */
	long ttl() default 10L;

	/**
	 * time unit，default minute
	 *
	 * @return TimeUnit
	 */
	TimeUnit timeUnit() default TimeUnit.MINUTES;
}

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

import java.util.concurrent.TimeUnit;

/**
 * @author kyleju
 */
public interface IRedisLimit {

	/**
	 * limit logic
	 *
	 * @param key      key
	 * @param max      max number
	 * @param ttl      window
	 * @param timeUnit window time unit
	 * @return
	 */
	Long limit(String key, long max, long ttl, TimeUnit timeUnit);

}

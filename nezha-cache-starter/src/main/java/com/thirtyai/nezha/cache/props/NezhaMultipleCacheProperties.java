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
package com.thirtyai.nezha.cache.props;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * nezha multiple cache properties
 *
 * @author kyleju
 */
@ConfigurationProperties(value = "nezha.cache")
@Setter
@Getter
public class NezhaMultipleCacheProperties {
	/**
	 * cache max size
	 */
	private int cacheMaxSize = -1;
	/**
	 * allow null values
	 */
	private boolean allowNullValues = false;
	/**
	 * caffeine cache items
	 */
	private List<CacheItem> caffeine;
	/**
	 * redis cache items
	 */
	private List<CacheItem> redis;
	/**
	 * caffeine redis cache items
	 */
	private List<CacheItem> caffeineRedis;

	@Data
	public static class CacheItem {
		private static final int TWO = 2;
		private static final int FOUR = 4;
		private String name;
		private int ttlSeconds = 300;
		private int refreshTriggerSeconds = -1;
		private int refresherSleepSeconds = 5;
		/**
		 * auto refresh default enabled
		 */
		private boolean enabledAutoRefresh = true;

		public boolean validateSelf() throws Exception {
			/*
			 # rts >= 2 * sleepTime
			 # ttl >= 4 * sleepTime
			 # ttl >= rts > ttl/2
			 */
			if (StrUtil.isBlank(name)) {
				throw new Exception("name is blank.");
			}
			if (ttlSeconds <= 0) {
				throw new Exception("the ttlSeconds is less than zero.");
			}
			if (this.refreshTriggerSeconds > 0) {
				if (ttlSeconds < refresherSleepSeconds * FOUR) {
					throw new Exception("the ttlSeconds must greater than 4 * refresherSleepSeconds.");
				}
				if (refreshTriggerSeconds < refresherSleepSeconds * TWO) {
					throw new Exception("the refreshTriggerSeconds must greater than 2 * refresherSleepSeconds.");
				}
			}
			return true;
		}
	}
}

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
package com.thirtyai.nezha.cache.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Cache key operate message
 *
 * @author kyleju
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CacheKeyOperateMessage implements Serializable {
	/**
	 * cache name
	 */
	private String cacheName;
	/**
	 * key
	 */
	private String key;
	/**
	 * operate message type {@link OperateMessageType}
	 */
	private OperateMessageType operateMessageType = OperateMessageType.Clear;

}

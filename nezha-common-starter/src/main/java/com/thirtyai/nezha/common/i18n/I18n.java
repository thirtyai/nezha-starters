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
package com.thirtyai.nezha.common.i18n;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.common.util.YamlUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * I18n status enum
 *
 * @author kyleju
 */
@Slf4j
public abstract class I18n {
	private static final Boolean LOCK = false;
	@Getter
	private final static Map<String, I18n> I18N_MAPS = new ConcurrentHashMap<>();

	private final String identifyName;

	@Getter
	private final String name;

	@Getter
	private final String code;

	private final String desc;

	public String getFullIdentifyName() {
		return this.name + StrUtil.DOT + identifyName;
	}

	/**
	 * init i18n
	 *
	 * @param identifyName identify name
	 * @param name         name
	 * @param code         code
	 * @param desc		   desc
	 */
	public I18n(String identifyName, String name, String code, String desc) {
		this.name = name;
		this.code = code;
		this.desc = desc;
		this.identifyName = identifyName;
		synchronized (LOCK) {
			if (!I18N_MAPS.containsKey(this.getFullIdentifyName())) {
				I18N_MAPS.remove(this.getFullIdentifyName());
			}
			I18N_MAPS.put(this.getFullIdentifyName(), this);
		}
	}

	/**
	 * get i18n
	 *
	 * @param name name
	 * @return i18n {@link I18n}
	 */
	public static I18n valueOf(String name) {
		return I18N_MAPS.get(name);
	}

	/**
	 * i18nWrapper map
	 */
	private static final Map<String, Map<String, I18nWrapper.I18nItem>> I18N_WRAPPER_MAP = new HashMap<>();

	@Setter
	@Getter
	private String defaultLang;

	public static void baseInit(String identifyName, String path, Resource[] resources) {
		Arrays.stream(resources).forEach(resource -> {
			try {
				if (resource.getURL().toString().contains(path)) {
					I18nWrapper i18nWrapper = YamlUtil.parse(resource.getInputStream(), I18nWrapper.class);
					Map<String, I18nWrapper.I18nItem> i18nItemMap = MapUtil.newHashMap();
					if (i18nWrapper != null && i18nWrapper.getI18ns() != null && i18nWrapper.getI18ns().size() > 0) {
						i18nWrapper.getI18ns().forEach(item -> i18nItemMap.put(item.getName() + StrUtil.DOT + identifyName, item));
						String key = Objects.requireNonNull(resource.getFilename()).split("\\.")[0].toLowerCase();
						if (I18N_WRAPPER_MAP.get(key) == null) {
							I18N_WRAPPER_MAP.put(key, i18nItemMap);
						} else {
							I18N_WRAPPER_MAP.get(key).putAll(i18nItemMap);
						}
					}
				}
			} catch (IOException ignored) {
			}
		});
	}

	public boolean equals(I18n i18n) {
		return this.getFullIdentifyName() != null && i18n != null && this.getFullIdentifyName().equals(i18n.getFullIdentifyName());
	}

	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * get desc with default language
	 *
	 * @return ignore
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * get desc by local
	 *
	 * @param local local
	 * @return ignore
	 */
	public String getDesc(String local) {
		if (StrUtil.isBlank(local)) {
			return desc;
		}
		if (defaultLang.equals(local)) {
			return desc;
		}
		String val;
		Map<String, I18nWrapper.I18nItem> itemMap = getI18nMap(local);
		I18nWrapper.I18nItem i18nItem = null;
		if (itemMap != null) {
			i18nItem = itemMap.get(this.getFullIdentifyName());
		}
		if (i18nItem == null) {
			val = desc;
		} else {
			val = i18nItem.getDesc();
		}
		if (val == null) {
			val = "";
		}
		return val;
	}

	private Map<String, I18nWrapper.I18nItem> getI18nMap(String local) {
		if (I18N_WRAPPER_MAP.containsKey(local.toLowerCase())) {
			return I18N_WRAPPER_MAP.get(local.toLowerCase());
		}
		return I18N_WRAPPER_MAP.get(this.getDefaultLang());
	}
}

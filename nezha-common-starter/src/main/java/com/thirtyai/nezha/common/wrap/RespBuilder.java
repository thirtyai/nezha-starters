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
package com.thirtyai.nezha.common.wrap;

import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.common.i18n.I18n;

/**
 * Resp builder
 *
 * @author kyleju
 */
public class RespBuilder {
	public final static int TOTAL_DEFAULT_VALUE = NezhaConstant.DEFAULT_VALUE;

	/**
	 * build Resp object
	 *
	 * @param httpStatus http status
	 * @param code       application level message code
	 * @param desc       code description
	 * @param data       object T
	 * @param total      total, if T is part of all of list.
	 * @param <T>        T
	 * @return Resp
	 */
	public static <T> Resp<T> build(int httpStatus, String code, String desc, T data, Integer total) {
		int totalTemp = total == null ? TOTAL_DEFAULT_VALUE : total;
		return new Resp<T>().setCode(code).setDesc(desc).setData(data).setStatus(httpStatus).setTotal(totalTemp);
	}

	/**
	 * build Resp object
	 *
	 * @param httpStatus http status
	 * @param i18n       i18n {@link I18n}
	 * @param data       object T
	 * @param total      total, if T is part of all of list
	 * @param locale     locale
	 * @param <T>        T
	 * @return Resp
	 */
	public static <T> Resp<T> build(int httpStatus, I18n i18n, T data, Integer total, String locale) {
		String desc;
		if (StrUtil.isEmpty(locale)) {
			desc = i18n.getDesc();
		} else {
			desc = i18n.getDesc(locale);
		}
		return build(httpStatus, i18n == null ? "" : i18n.getCode(), i18n == null ? "" : desc, data, total);
	}


	/**
	 * build Resp object
	 *
	 * @param httpStatus http status
	 * @param i18n       i18n {@link I18n}
	 * @param data       object T
	 * @param locale     locale
	 * @param <T>        T
	 * @return Resp
	 */
	public static <T> Resp<T> build(int httpStatus, I18n i18n, T data, String locale) {
		String desc;
		if (StrUtil.isEmpty(locale)) {
			desc = i18n.getDesc();
		} else {
			desc = i18n.getDesc(locale);
		}
		return build(httpStatus, i18n == null ? "" : i18n.getCode(), i18n == null ? "" : desc, data, TOTAL_DEFAULT_VALUE);
	}

	/**
	 * build success
	 *
	 * @param i18n   i18n {@link I18n}
	 * @param data   object T
	 * @param total  total, if T is part of all of list
	 * @param locale locale
	 * @param <T>    t
	 * @return Resp
	 */
	public static <T> Resp<T> buildHttpSuccess(I18n i18n, T data, Integer total, String locale) {
		return build(200, i18n, data, total, locale);
	}

	/**
	 * build Success
	 *
	 * @param identifyName string
	 * @param data         object T
	 * @param total        total, if T is part of all of list
	 * @param locale       locale
	 * @param <T>          T
	 * @return Resp
	 */
	public static <T> Resp<T> buildHttpSuccess(String identifyName, T data, Integer total, String locale) {
		return build(200, I18n.valueOf(identifyName), data, total, locale);
	}
}

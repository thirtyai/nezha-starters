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
package com.thirtyai.nezha.common.lang;

import cn.hutool.core.util.ReUtil;

/**
 * regex pool (from hutool for spring validation)
 *
 * @author kyleju
 */
public class RegexPool {

	/**
	 * 英文字母 、数字和下划线
	 */
	public final static String GENERAL = "^\\w+$";
	/**
	 * 数字
	 */
	public final static String NUMBERS = "\\d+";
	/**
	 * 字母
	 */
	public final static String WORD = "[a-zA-Z]+";
	/**
	 * 单个中文汉字
	 */
	public final static String CHINESE = ReUtil.RE_CHINESE;
	/**
	 * 中文汉字
	 */
	public final static String CHINESES = ReUtil.RE_CHINESES;
	/**
	 * 分组
	 */
	public final static String GROUP_VAR = "\\$(\\d+)";
	/**
	 * IP v4
	 */
	public final static String IPV4 = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
	/**
	 * IP v6
	 */
	public final static String IPV6 = "(([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9])?[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9])?[0-9]))";
	/**
	 * 货币
	 */
	public final static String MONEY = "^(\\d+(?:\\.\\d+)?)$";
	/**
	 * 邮件，符合RFC 5322规范，正则来自：http://emailregex.com/
	 * Pattern.CASE_INSENSITIVE
	 * // public final static String EMAIL = "(\\w|.)+@\\w+(\\.\\w+){1,2}";
	 */
	public final static String EMAIL = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
	/**
	 * 移动电话
	 */
	public final static String MOBILE = "(?:0|86|\\+86)?1[3456789]\\d{9}";
	/**
	 * 18位身份证号码
	 */
	public final static String CITIZEN_ID = "[1-9]\\d{5}[1-2]\\d{3}((0\\d)|(1[0-2]))(([012]\\d)|3[0-1])\\d{3}(\\d|X|x)";
	/**
	 * 邮编
	 */
	public final static String ZIP_CODE = "[1-9]\\d{5}(?!\\d)";
	/**
	 * 生日
	 */
	public final static String BIRTHDAY = "^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$";
	/**
	 * URL
	 */
	public final static String URL = "[a-zA-z]+://[^\\s]*";
	/**
	 * Http URL
	 */
	public final static String URL_HTTP = "(https://|http://)?([\\w-]+\\.)+[\\w-]+(:\\d+)*(/[\\w- ./?%&=]*)?";
	/**
	 * 中文字、英文字母、数字和下划线
	 */
	public final static String GENERAL_WITH_CHINESE = "^[\u4E00-\u9FFF\\w]+$";
	/**
	 * UUID
	 */
	public final static String UUID = "^[0-9a-z]{8}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{4}-[0-9a-z]{12}$";
	/**
	 * 不带横线的UUID
	 */
	public final static String UUID_SIMPLE = "^[0-9a-z]{32}$";
	/**
	 * MAC地址正则
	 * Pattern.CASE_INSENSITIVE
	 */
	public final static String MAC_ADDRESS = "((?:[A-F0-9]{1,2}[:-]){5}[A-F0-9]{1,2})|(?:0x)(\\d{12})(?:.+ETHER)";
	/**
	 * 16进制字符串
	 * Pattern.CASE_INSENSITIVE
	 */
	public final static String HEX = "^[a-f0-9]+$";
	/**
	 * 时间正则
	 */
	public final static String TIME = "\\d{1,2}:\\d{1,2}(:\\d{1,2})?";
	/**
	 * 中国车牌号码（兼容新能源车牌）
	 * //https://gitee.com/loolly/hutool/issues/I1B77H?from=project-issue
	 * //https://gitee.com/loolly/hutool/issues/I1BJHE?from=project-issue
	 */
	public final static String PLATE_NUMBER =
		"^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[ABCDEFGHJK])|([ABCDEFGHJK]([A-HJ-NP-Z0-9])[0-9]{4})))|" +
			"([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领]\\d{3}\\d{1,3}[领])|" +
			"([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$";


	/**
	 * 社会统一信用代码
	 * <pre>
	 * 第一部分：登记管理部门代码1位 (数字或大写英文字母)
	 * 第二部分：机构类别代码1位 (数字或大写英文字母)
	 * 第三部分：登记管理机关行政区划码6位 (数字)
	 * 第四部分：主体标识码（组织机构代码）9位 (数字或大写英文字母)
	 * 第五部分：校验码1位 (数字或大写英文字母)
	 * </pre>
	 */
	public final static String CREDIT_CODE = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";

}

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
package com.thirtyai.nezha.core.web.filter;

import cn.hutool.core.collection.CollUtil;
import com.thirtyai.nezha.common.NezhaConstant;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.ServletException;
import java.util.Arrays;
import java.util.List;

/**
 * nezha abstract web filter
 *
 * @author kyleju
 */
public abstract class AbstractNezhaWebFilter extends OncePerRequestFilter {

	private final List<String> mStaticExclusions;

	private List<String> exclusions;

	private final AntPathMatcher antPathMatcher = new AntPathMatcher();

	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();
		antPathMatcher.setCachePatterns(true);
		antPathMatcher.setCaseSensitive(true);
		antPathMatcher.setTrimTokens(true);
		exclusions = getExclusions(this.mStaticExclusions, NezhaConstant.DEFAULT_STATIC_EXCLUSIONS);
	}

	public AbstractNezhaWebFilter(List<String> staticExclusions) {
		this.mStaticExclusions = staticExclusions;
	}

	/**
	 * ignore or not
	 *
	 * @param path path
	 * @return ignore
	 */
	public boolean isIgnore(String path) {
		return exclusions.stream().anyMatch(pattern -> antPathMatcher.match(pattern, path));
	}

	/**
	 * get exclusion file list
	 *
	 * @param configExclusions config exclusions
	 * @param strings          strings
	 * @return ignore
	 */
	public List<String> getExclusions(List<String> configExclusions, String[] strings) {
		if (strings != null && strings.length > 0) {
			return CollUtil.addAllIfNotContains(configExclusions, Arrays.asList(strings));
		}
		return configExclusions;
	}
}

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

import com.thirtyai.nezha.core.web.props.NezhaWebProperties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * nezha web filter
 *
 * @author kyle ju
 */
public class NezhaWebFilter extends AbstractNezhaWebFilter {

	public final static String NEZHA_WEB_FILTER_NAME = "nezha-web-filter";
	public final static int NEZHA_WEB_FILTER_ORDER_STEP = 1000;

	public NezhaWebFilter(NezhaWebProperties nezhaWebProperties) {
		super(nezhaWebProperties.getStaticExclusions());
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (isIgnore(request.getServletPath())) {
			filterChain.doFilter(request, response);
		} else {
			NezhaWebRequestWrapper requestWrapper = new NezhaWebRequestWrapper(request);
			filterChain.doFilter(requestWrapper, response);
		}
	}
}

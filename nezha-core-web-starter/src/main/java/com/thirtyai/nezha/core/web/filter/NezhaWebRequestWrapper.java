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
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.thirtyai.nezha.core.web.utils.RequestUtil;
import org.springframework.http.MediaType;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * nezha web request wrapper
 * with xss encode and reread body and params
 *
 * @author kyleju
 */
public class NezhaWebRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * cache body
	 */
	private final byte[] body;

	/**
	 * request params maps
	 */
	private Map<String, String[]> requestParamMaps;

	public NezhaWebRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		boolean isRawBody = false;
		if (request.getContentType() != null) {
			MediaType mediaType = MediaType.parseMediaType(request.getContentType());
			switch (mediaType.getType()) {
				case "application":
				case "text":
				case "image":
					isRawBody = true;
					break;
				default:
					isRawBody = false;
			}
		}

		requestParamMaps = request.getParameterMap();
		Map<String, String> maps = new HashMap<>();
		requestParamMaps.forEach(((key, item) -> maps.put(xssEncode(key), CollUtil.join(Arrays.stream(item).map(obj -> xssEncode(obj)).collect(Collectors.toList()), ","))));

		if (isRawBody) {
			body = RequestUtil.getRequestBytes(request);
		} else {
			List<String> lists = new ArrayList<>();
			maps.forEach((key, item) -> lists.add(key + "=" + item));
			body = CollUtil.join(lists, "&").getBytes(CharsetUtil.UTF_8);
		}
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.body);
		return new ServletInputStream() {

			@Override
			public boolean isFinished() {
				return true;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
			}

			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
		};

	}

	@Override
	public String getParameter(String name) {
		if ((requestParamMaps != null && requestParamMaps.containsKey(xssEncode(name)))) {
			return (requestParamMaps.get(xssEncode(name)) != null && requestParamMaps.get(xssEncode(name)).length > 0) ? xssEncode(requestParamMaps.get(xssEncode(name))[0]) : null;
		}
		return null;
	}

	@Override
	public String[] getParameterValues(String name) {
		if ((requestParamMaps != null && requestParamMaps.containsKey(xssEncode(name)))) {
			return requestParamMaps.get(xssEncode(name));
		}
		return null;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return requestParamMaps;
	}

	@Override
	public String getHeader(String name) {
		String value = super.getHeader(xssEncode(name));
		if (StrUtil.isNotBlank(value)) {
			value = xssEncode(value);
		}
		return value;
	}

	private String xssEncode(String input) {
		return HtmlUtil.escape(input);
	}
}

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
package com.thirtyai.nezha.core.web.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProviders;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.DefaultErrorViewResolver;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * nezha error view resolver
 *
 * @author kyleju
 */
@Component
public class NezhaErrorViewResolver implements ErrorViewResolver, Ordered {
	private static final Map<HttpStatus.Series, String> SERIES_VIEWS;

	static {
		Map<HttpStatus.Series, String> views = new EnumMap<>(HttpStatus.Series.class);
		views.put(HttpStatus.Series.CLIENT_ERROR, "4xx");
		views.put(HttpStatus.Series.SERVER_ERROR, "5xx");
		SERIES_VIEWS = Collections.unmodifiableMap(views);
	}

	private ApplicationContext applicationContext;

	private final ResourceProperties resourceProperties;

	private final TemplateAvailabilityProviders templateAvailabilityProviders;

	private int order = Ordered.LOWEST_PRECEDENCE;

	/**
	 * Create a new {@link DefaultErrorViewResolver} instance.
	 *
	 * @param applicationContext the source application context
	 * @param resourceProperties resource properties
	 */
	@Autowired
	public NezhaErrorViewResolver(ApplicationContext applicationContext, ResourceProperties resourceProperties) {
		Assert.notNull(applicationContext, "ApplicationContext must not be null");
		Assert.notNull(resourceProperties, "ResourceProperties must not be null");
		this.applicationContext = applicationContext;
		this.resourceProperties = resourceProperties;
		this.templateAvailabilityProviders = new TemplateAvailabilityProviders(applicationContext);
	}

	NezhaErrorViewResolver(ApplicationContext applicationContext, ResourceProperties resourceProperties,
						   TemplateAvailabilityProviders templateAvailabilityProviders) {
		Assert.notNull(applicationContext, "ApplicationContext must not be null");
		Assert.notNull(resourceProperties, "ResourceProperties must not be null");
		this.applicationContext = applicationContext;
		this.resourceProperties = resourceProperties;
		this.templateAvailabilityProviders = templateAvailabilityProviders;
	}

	@Override
	public ModelAndView resolveErrorView(HttpServletRequest request, HttpStatus status, Map<String, Object> model) {
		ModelAndView modelAndView = resolve(String.valueOf(status.value()), model);
		if (modelAndView == null && SERIES_VIEWS.containsKey(status.series())) {
			modelAndView = resolve(SERIES_VIEWS.get(status.series()), model);
		}
		return modelAndView;
	}

	private ModelAndView resolve(String viewName, Map<String, Object> model) {
		String errorViewName = "error/" + viewName;
		TemplateAvailabilityProvider provider = this.templateAvailabilityProviders.getProvider(errorViewName,
			this.applicationContext);
		if (provider != null) {
			return new ModelAndView(errorViewName, model);
		}
		return resolveResource(errorViewName, model);
	}

	private ModelAndView resolveResource(String viewName, Map<String, Object> model) {
		for (String location : this.resourceProperties.getStaticLocations()) {
			try {
				Resource resource = this.applicationContext.getResource(location);
				resource = resource.createRelative(viewName + ".html");
				if (resource.exists()) {
					return new ModelAndView(new NezhaErrorViewResolver.HtmlResourceView(resource), model);
				}
			} catch (Exception ex) {
			}
		}
		return null;
	}

	/**
	 * get default default nezha model and view
	 *
	 * @param viewName view name
	 * @param model    model
	 * @return ignore
	 */
	@Deprecated
	private ModelAndView getDefaultNezhaModelAndView(String viewName, Map<String, Object> model) {
		ClassPathResource classPathResource = new ClassPathResource("static/" + viewName + ".html");
		if (classPathResource.exists()) {
			return new ModelAndView(new NezhaErrorViewResolver.HtmlResourceView(classPathResource), model);
		}
		return null;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * {@link View} backed by an HTML resource.
	 */
	private static class HtmlResourceView implements View {

		private final Resource resource;

		HtmlResourceView(Resource resource) {
			this.resource = resource;
		}

		@Override
		public String getContentType() {
			return MediaType.TEXT_HTML_VALUE;
		}

		@Override
		public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			response.setContentType(getContentType());
			AtomicReference<String> result = new AtomicReference<>(new BufferedReader(new InputStreamReader(this.resource.getInputStream(), StandardCharsets.UTF_8))
				.lines().parallel().collect(Collectors.joining(System.lineSeparator())));
			if (model != null) {
				model.forEach((key, item) -> {
					result.set(result.get().replace(String.format("${%s}", key), item.toString()));
				});

				FileCopyUtils.copy(new ByteArrayInputStream(result.get().getBytes(StandardCharsets.UTF_8)), response.getOutputStream());
			}
		}

	}
}

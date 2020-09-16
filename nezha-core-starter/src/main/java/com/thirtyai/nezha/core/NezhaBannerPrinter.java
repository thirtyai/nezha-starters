/*
 * Copyright (c) 2019-2020 ThirtyAi & kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.core;

import org.slf4j.Logger;
import org.springframework.boot.Banner;
import org.springframework.boot.ImageBanner;
import org.springframework.boot.ResourceBanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * nezha banner printer
 */
class NezhaBannerPrinter {

	static final String BANNER_LOCATION_PROPERTY = "spring.banner.location";

	static final String BANNER_IMAGE_LOCATION_PROPERTY = "spring.banner.image.location";

	static final String DEFAULT_BANNER_LOCATION = "banner.txt";

	static final String[] IMAGE_EXTENSION = {"gif", "jpg", "png"};

	private final ResourceLoader resourceLoader;


	public NezhaBannerPrinter(ResourceLoader resourceLoader) {
		this.resourceLoader = (resourceLoader != null) ? resourceLoader
			: new DefaultResourceLoader(getClassLoader());
	}

	void print(Environment environment, Class<?> sourceClass, @NonNull Logger logger) {
		Banner banner = getBanner(environment);
		try {
			if (banner != null) {
				logger.info(createStringFromBanner(banner, environment, sourceClass));
			}
		} catch (UnsupportedEncodingException ex) {
			logger.warn("Failed to create String for banner", ex);
		}
	}

	void print(Environment environment, Class<?> sourceClass, PrintStream out) {
		Banner banner = getBanner(environment);
		if (banner != null) {
			banner.printBanner(environment, sourceClass, out);
		}
	}

	private Banner getBanner(Environment environment) {
		Banners banners = new Banners();
		banners.addIfNotNull(getImageBanner(environment));
		banners.addIfNotNull(getTextBanner(environment));
		if (banners.hasAtLeastOneBanner()) {
			return banners;
		}
		return null;
	}

	private Banner getTextBanner(Environment environment) {
		String location = environment.getProperty(BANNER_LOCATION_PROPERTY, DEFAULT_BANNER_LOCATION);
		Resource resource = this.resourceLoader.getResource(location);
		if (resource.exists()) {
			return new ResourceBanner(resource);
		}
		return null;
	}

	private Banner getImageBanner(Environment environment) {
		String location = environment.getProperty(BANNER_IMAGE_LOCATION_PROPERTY);
		if (StringUtils.hasLength(location)) {
			Resource resource = this.resourceLoader.getResource(location);
			return resource.exists() ? new ImageBanner(resource) : null;
		}
		for (String ext : IMAGE_EXTENSION) {
			Resource resource = this.resourceLoader.getResource("banner." + ext);
			if (resource.exists()) {
				return new ImageBanner(resource);
			}
		}
		return null;
	}

	private String createStringFromBanner(Banner banner, Environment environment, Class<?> mainApplicationClass)
		throws UnsupportedEncodingException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		banner.printBanner(environment, mainApplicationClass, new PrintStream(byteArrayOutputStream));
		String charset = environment.getProperty("spring.banner.charset", "UTF-8");
		return byteArrayOutputStream.toString(charset);
	}

	private ClassLoader getClassLoader() {
		if (this.resourceLoader != null) {
			return this.resourceLoader.getClassLoader();
		}
		return ClassUtils.getDefaultClassLoader();
	}

	/**
	 * {@link Banner} comprised of other {@link Banner Banners}.
	 */
	private static class Banners implements Banner {

		private final List<Banner> banners = new ArrayList<>();

		void addIfNotNull(Banner banner) {
			if (banner != null) {
				this.banners.add(banner);
			}
		}

		boolean hasAtLeastOneBanner() {
			return !this.banners.isEmpty();
		}

		@Override
		public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
			for (Banner banner : this.banners) {
				banner.printBanner(environment, sourceClass, out);
			}
		}

	}
}

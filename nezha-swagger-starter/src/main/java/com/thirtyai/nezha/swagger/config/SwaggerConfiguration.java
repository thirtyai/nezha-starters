/*
 * Copyright (c) kyle ju(nezha@thirtyai.com) All rights reserved.
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
package com.thirtyai.nezha.swagger.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import com.thirtyai.nezha.swagger.props.SwaggerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger configuration
 *
 * @author kyleju
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@EnableConfigurationProperties(SwaggerProperties.class)
@ConditionalOnProperty(prefix = "nezha.starters", name = "swagger", havingValue = "true")
public class SwaggerConfiguration {
	@Bean
	public Docket apis(SwaggerProperties swaggerProperties) {

		if (swaggerProperties.getPaths().size() == 0) {
			swaggerProperties.getPaths().add("/**");
		}
		List<Predicate<String>> paths = new ArrayList<>();
		swaggerProperties.getPaths().forEach(path -> paths.add(PathSelectors.ant(path)));

		if (swaggerProperties.getExcludePaths().size() == 0) {
			swaggerProperties.getExcludePaths().add("/error");
		}
		List<Predicate<String>> excludePaths = new ArrayList<>();
		swaggerProperties.getExcludePaths().forEach(path -> excludePaths.add(PathSelectors.ant(path)));

		return new Docket(DocumentationType.SWAGGER_2)
			.produces(Sets.newHashSet("application/json"))
			.consumes(Sets.newHashSet("application/json"))
			.protocols(Sets.newHashSet("http", "https"))
			.host(swaggerProperties.getHost())
			.apiInfo(getApiInfo(swaggerProperties)).select()
			.apis(getPackages(swaggerProperties.getPackages()))
			.paths(Predicates.and(Predicates.not(Predicates.or(excludePaths)), Predicates.or(paths)))
			.build();
	}

	private ApiInfo getApiInfo(SwaggerProperties swaggerProperties) {
		return new ApiInfoBuilder()
			.title(swaggerProperties.getTitle())
			.description(swaggerProperties.getDescription())
			.license(swaggerProperties.getLicense())
			.licenseUrl(swaggerProperties.getLicenseUrl())
			.termsOfServiceUrl(swaggerProperties.getTermsUrl())
			.contact(new Contact(swaggerProperties.getContact().getName(), swaggerProperties.getContact().getUrl(), swaggerProperties.getContact().getEmail()))
			.version(swaggerProperties.getVersion())
			.build();
	}

	public static Predicate<RequestHandler> getPackages(final List<String> packages) {
		return input -> declaringClass(input).transform(handlerPackage(packages)).or(true);
	}

	private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
		return Optional.fromNullable(input.declaringClass());
	}

	private static Function<Class<?>, Boolean> handlerPackage(final List<String> packages) {
		return input -> {
			for (String strPackage : packages) {
				boolean isMatch = input.getPackage().getName().startsWith(strPackage);
				if (isMatch) {
					return true;
				}
			}
			return false;
		};
	}
}

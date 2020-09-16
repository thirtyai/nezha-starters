/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.thirtyai.nezha.druid.config.stat;

import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import lombok.RequiredArgsConstructor;
import org.aopalliance.aop.Advice;
import org.codehaus.commons.nullanalysis.NotNull;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.RegexpMethodPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lihengming [89921218@qq.com]
 */
@Configuration
@ConditionalOnProperty("spring.datasource.druid.aop-patterns")
@RequiredArgsConstructor
public class DruidSpringAopConfiguration {
	@NotNull
	DruidStatProperties properties;
	@NotNull
	Advice advice;

	@Bean
	@ConditionalOnMissingBean
	public Advice advice() {
		return new DruidStatInterceptor();
	}

	@Bean
	@ConditionalOnMissingBean
	public Advisor advisor() {
		return new RegexpMethodPointcutAdvisor(properties.getAopPatterns(), advice);
	}

	@Bean
	@ConditionalOnProperty(name = "spring.aop.auto", havingValue = "false")
	@ConditionalOnMissingBean
	public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		advisorAutoProxyCreator.setProxyTargetClass(true);
		return advisorAutoProxyCreator;
	}
}

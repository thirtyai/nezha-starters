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
package com.thirtyai.nezha.redis.limit;

import cn.hutool.core.util.StrUtil;
import com.thirtyai.nezha.i18n.Status;
import com.thirtyai.nezha.common.NezhaConstant;
import com.thirtyai.nezha.common.exception.I18nException;
import com.thirtyai.nezha.common.spel.AbstractExpressionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Rate limit aspect
 *
 * @author kyleju
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
@Component
public class RateLimitAspect extends AbstractExpressionEvaluator {
	private final IRedisLimit redisLimitService;

	@Around("@annotation(com.thirtyai.nezha.redis.limit.RateLimit))")
	public Object around(ProceedingJoinPoint pjp) throws Throwable {
		Method method = ((MethodSignature) pjp.getSignature()).getMethod();
		RateLimit rateLimit = method.getAnnotation(RateLimit.class);
		String key = rateLimit.key();
		if (StrUtil.isBlank(key)) {
			throw new I18nException(Status.Redis_Limit_Key_Is_Blank);
		}

		key = NezhaConstant.nezhaPreFix(key);

		if (StrUtil.isNotBlank(rateLimit.params())) {
			String evalParam = evalSePlParam(pjp, rateLimit.params());
			if (StrUtil.isNotBlank(evalParam)) {
				key = key + StrUtil.C_UNDERLINE + evalParam;
			}
		}

		Long limit = redisLimitService.limit(key, rateLimit.max(), rateLimit.ttl(), rateLimit.timeUnit());
		if (limit == 0) {
			throw new I18nException(Status.Redis_Limit_Is_Limit);
		}

		try {
			Object rawResult = pjp.proceed();
			return rawResult;
		} catch (Exception ex) {
			throw ex;
		}
	}
}

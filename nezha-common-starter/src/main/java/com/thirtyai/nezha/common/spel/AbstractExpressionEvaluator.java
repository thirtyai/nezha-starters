/*
 * Copyright (c) 2019-2020 kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
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
package com.thirtyai.nezha.common.spel;

import com.thirtyai.nezha.common.util.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

import java.lang.reflect.Method;

/**
 * Abstract expression evaluator
 *
 * @author kyleju
 */
public class AbstractExpressionEvaluator {
	private final MapCacheExpressionEvaluator mapCacheExpressionEvaluator = new MapCacheExpressionEvaluator();

	/**
	 * eval SePL Param
	 *
	 * @param proceedingJoinPoint proceeding join point
	 * @param param               param
	 * @return value
	 */
	public String evalSePlParam(ProceedingJoinPoint proceedingJoinPoint, String param) {
		MethodSignature ms = (MethodSignature) proceedingJoinPoint.getSignature();
		Method method = ms.getMethod();
		Object[] args = proceedingJoinPoint.getArgs();
		Object target = proceedingJoinPoint.getTarget();
		Class<?> targetClass = target.getClass();
		EvaluationContext context = mapCacheExpressionEvaluator.createContext(method, args, target, targetClass, SpringUtil.getApplicationContext());
		AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
		return mapCacheExpressionEvaluator.evalAsText(param, elementKey, context);
	}
}

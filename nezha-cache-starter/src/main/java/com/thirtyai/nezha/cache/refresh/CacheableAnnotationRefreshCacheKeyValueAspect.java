package com.thirtyai.nezha.cache.refresh;

import cn.hutool.core.collection.CollUtil;
import com.thirtyai.nezha.common.spel.AbstractExpressionEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Cacheable annotation refresh aspect
 *
 * @author kyleju
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class CacheableAnnotationRefreshCacheKeyValueAspect extends AbstractExpressionEvaluator {

	private final ICacheKeyValueRefreshSupport cacheRefreshSupport;

	private List<Cacheable> getMethodAnnotations(AnnotatedElement ae) {
		List<Cacheable> annotations = new ArrayList<Cacheable>(2);
		// look for raw annotation
		Cacheable ann = ae.getAnnotation(Cacheable.class);
		if (ann != null) {
			annotations.add(ann);
		}
		// look for meta-annotations
		for (Annotation metaAnn : ae.getAnnotations()) {
			ann = metaAnn.annotationType().getAnnotation(Cacheable.class);
			if (ann != null) {
				annotations.add(ann);
			}
		}
		return (annotations.isEmpty() ? null : annotations);
	}

	private Method getSpecificMethod(ProceedingJoinPoint pjp) {
		MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
		Method method = methodSignature.getMethod();
		// The method may be on an interface, but we need attributes from the
		// target class. If the target class is null, the method will be
		// unchanged.
		Class<?> targetClass = AopProxyUtils.ultimateTargetClass(pjp.getTarget());
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
		// If we are dealing with method with generic parameters, find the
		// original method.
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		return specificMethod;
	}

	@Pointcut(value = "@annotation(org.springframework.cache.annotation.Cacheable)")
	public void pointcut() {
	}

	@Around("pointcut()")
	public Object registerInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
		Method method = this.getSpecificMethod(joinPoint);
		List<Cacheable> annotations = this.getMethodAnnotations(method);
		if (annotations != null) {
			for (Cacheable cacheable : annotations) {
				List<String> lists = CollUtil.addAllIfNotContains(Arrays.asList(cacheable.value()), Arrays.asList(cacheable.cacheNames()));
				cacheRefreshSupport.add(this.evalSePlParam(joinPoint, cacheable.key()), joinPoint.getTarget(), method, joinPoint.getArgs(), lists);
			}
		}
		return joinPoint.proceed();
	}
}

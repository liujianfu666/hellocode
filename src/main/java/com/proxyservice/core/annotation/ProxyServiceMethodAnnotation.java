package com.proxyservice.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.proxyservice.core.handler.AsyncMethodHandler;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * @author liu_jianfu
 *
 */
public @interface ProxyServiceMethodAnnotation {

	String value() default "";
	/**
	 * 方法类型,默认为同步调用
	 * @return
	 */
	MethodTypeEnum methodType() default MethodTypeEnum.SYNC;
	/**
	 * 异步调用时的回调方法<br>
	 * 如果methodType为MethodTypeEnum.ASYNC 此值和handelBeanName必需指定一个
	 * @return
	 */
	Class<? extends AsyncMethodHandler> handelBeanClass() default AsyncMethodHandler.class;
	/**
	 * 异步调用时的回调bean,bean必须实现AsyncMethodHandler接口<br>
	 * 如果methodType为MethodTypeEnum.ASYNC 此值和handelBeanName必需指定一个
	 * @return
	 */
	String handelBeanName() default "";
}

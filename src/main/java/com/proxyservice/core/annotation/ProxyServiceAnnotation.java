package com.proxyservice.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.proxyservice.core.handler.ServiceHandler;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
/**
 * @author liu_jianfu
 *
 */
public @interface ProxyServiceAnnotation {
	/**
	 * 该接口代理由那一个类来回调处理,如果handelBeanName不为空,该属性失效
	 * @return
	 */
	Class<? extends ServiceHandler> handelBeanClass() default ServiceHandler.class;
	/**
	 * 该接口代理由哪一个bean来回调处理
	 * @return
	 */
	String handelBeanName() default "";
}

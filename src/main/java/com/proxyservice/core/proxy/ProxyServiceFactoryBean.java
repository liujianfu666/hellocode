package com.proxyservice.core.proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
/**
 * @author liu_jianfu
 *
 * @param <T>
 */
public class ProxyServiceFactoryBean<T> implements FactoryBean<T> {

	private Class<T> serviceInterface;

	private static ApplicationContext applicationContext;
	public ProxyServiceFactoryBean (ApplicationContext applicationContext) {
		ProxyServiceFactoryBean.applicationContext = applicationContext; 
	}
	
	public ProxyServiceFactoryBean(Class<T> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	
	@Override
	public T getObject() throws Exception {
		return new ServiceProxyFactory<T>(serviceInterface,applicationContext).newInstance();
	}

	@Override
	public Class<?> getObjectType() {
		return this.serviceInterface;
	}
}

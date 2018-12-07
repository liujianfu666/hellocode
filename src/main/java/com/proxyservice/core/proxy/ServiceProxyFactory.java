package com.proxyservice.core.proxy;

import java.lang.reflect.Proxy;

import org.springframework.context.ApplicationContext;
/**
 * 
 * @author liu_jianfu
 *
 * @param <T>
 */
public class ServiceProxyFactory<T> {

	private final Class<T> serviceInterface;
	private ApplicationContext applicationContext;

	public ServiceProxyFactory(Class<T> serviceInterface,ApplicationContext applicationContext) {
		this.serviceInterface = serviceInterface;
		this.applicationContext = applicationContext;
	}

	public Class<T> getMapperInterface() {
		return serviceInterface;
	}

	@SuppressWarnings("unchecked")
	protected T newInstance(ServiceProxy<T> mapperProxy) {
		return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[] { serviceInterface },
				mapperProxy);
	}

	public T newInstance() {
		final ServiceProxy<T> serviceProxy = new ServiceProxy<T>(serviceInterface,applicationContext);
		return newInstance(serviceProxy);
	}
}

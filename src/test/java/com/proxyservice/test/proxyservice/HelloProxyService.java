package com.proxyservice.test.proxyservice;

import com.proxyservice.core.annotation.MethodTypeEnum;
import com.proxyservice.core.annotation.ProxyServiceAnnotation;
import com.proxyservice.core.annotation.ProxyServiceMethodAnnotation;
import com.proxyservice.test.proxyservice.handler.AsyncHelloServiceHandle;

/**
 * @ProxyServiceAnnotation 指定这是一个代理类
 * handelBeanName 属性指定处理代理逻辑的bean 
 * @author carryx
 *
 */
@ProxyServiceAnnotation(handelBeanName="myServiceHandler")
public interface HelloProxyService {

	/**
	 * 默主为同步调用,此时的返回结果有意义
	 * @param name
	 * @return
	 */
	public String syncHello(String name);
	/**
	 * 通过methodType=MethodTypeEnum.ASYNC设置为异步调用...handelBeanClass指定结果回调类
	 * @param name
	 */
	@ProxyServiceMethodAnnotation(methodType=MethodTypeEnum.ASYNC,handelBeanClass=AsyncHelloServiceHandle.class)
	public void asyncHello(String name);
	/**
	 * 通过methodType=MethodTypeEnum.NOANSWER指定为异步调用,且无需处理结果
	 * @param name
	 */
	@ProxyServiceMethodAnnotation(methodType=MethodTypeEnum.NOANSWER)
	public void noanswerHello(String name);
}

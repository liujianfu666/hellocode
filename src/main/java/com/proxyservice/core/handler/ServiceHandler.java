package com.proxyservice.core.handler;

import java.lang.reflect.Method;
/**
 * 代理服务处理接口
 * @author liu_jianfu
 *
 */
public interface ServiceHandler {

	public Object handel(Object proxy, Method method, Object[] args);
}

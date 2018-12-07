package com.proxyservice.core.handler;

import java.lang.reflect.Method;
/**
 * 
 *  @author liu_jianfu
 *
 */
public class SyncMethodHandlerModel {
	private Method method;
	private Object[] args;
	private Object result;
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Object[] getArgs() {
		return args;
	}
	public void setArgs(Object[] args) {
		this.args = args;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
}

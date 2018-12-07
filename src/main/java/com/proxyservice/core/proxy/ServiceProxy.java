package com.proxyservice.core.proxy;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import com.proxyservice.core.annotation.MethodTypeEnum;
import com.proxyservice.core.annotation.ProxyServiceAnnotation;
import com.proxyservice.core.annotation.ProxyServiceMethodAnnotation;
import com.proxyservice.core.handler.AsyncMethodHandler;
import com.proxyservice.core.handler.ServiceHandler;
import com.proxyservice.core.handler.SyncMethodHandlerModel;

/**
 * 
 * @author liu_jianfu
 *
 * @param <T>
 */
public class ServiceProxy<T> implements InvocationHandler {

	@SuppressWarnings("unused")
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	// 被代理的接口类
	private Class<T> serviceinterface;
	private ApplicationContext applicationContext;
	
	private ExecutorService cachedThreadPool;

	public ServiceProxy(Class<T> serviceinterface,ApplicationContext applicationContext) {
		this.serviceinterface = serviceinterface;
		this.applicationContext = applicationContext;
		boolean deamonhandler = Boolean.valueOf(applicationContext.getEnvironment().getProperty("proxyservice.deamonhandler"));
		
		cachedThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
	            Thread t = Executors.defaultThreadFactory().newThread(r);
	            t.setDaemon(deamonhandler);
	            return t;
	        }
		});
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			//如果调用的方法是Object定义的方法就不做代理
			if (Object.class.equals(method.getDeclaringClass())) {
				return method.invoke(this, args);
			} else if (isDefaultMethod(method)) { //如果调用的是代理类上的方法
				return invokeDefaultMethod(proxy, method, args);
			}
		} catch (Throwable t) {
			throw t;
		}
		//调用的是接口方法
		return excuteRemoteService(proxy, method, args);
	}

	/**
	 * 执行方法
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private Object excuteRemoteService(Object proxy, Method method, Object[] args) throws Exception {
		//获取方法(同步调用,异步回调,异步无返回)
		Method invokeMethod = getMethod(proxy,method,args);
		//远程调用
		return invokeMethod.invoke(this, proxy,method,args);
	}

	/**
	 * 对应方法的缓存
	 */
	private ConcurrentHashMap<Method,Method> methodCache = new ConcurrentHashMap<>();
	/**
	 * 获取执行方法
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private Method getMethod(Object proxy, Method method, Object[] args) throws NoSuchMethodException, SecurityException {
		//从缓存中获取,如果有就直接返回
		Method result = methodCache.get(method);
		if(null != result) {
			return result;
		}

		ProxyServiceMethodAnnotation annotation = method.getAnnotation(ProxyServiceMethodAnnotation.class);
		if(null == annotation || annotation.methodType() == MethodTypeEnum.SYNC) {
			//同步方法
			result = this.getClass().getDeclaredMethod("syncInvokeRemoteMethod", Object.class,Method.class,Object[].class);
		} 
		else if(annotation.methodType() == MethodTypeEnum.ASYNC) {
			//异步回调方法
			result = this.getClass().getDeclaredMethod("asyncInvokeRemoteMethod", Object.class,Method.class,Object[].class);
		} else if(annotation.methodType() == MethodTypeEnum.NOANSWER) {
			//无需返回的方法
			result = this.getClass().getDeclaredMethod("noAnswerInvokeRemoteMethod", Object.class,Method.class,Object[].class);
		}
		methodCache.put(method, result);
		return result;
	}
	
	/**
	 * 异步调用远程方法
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unused")
	private void asyncInvokeRemoteMethod(Object proxy, Method method, Object[] args) {
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				Object result = syncInvokeRemoteMethod(proxy,method,args);
				SyncMethodHandlerModel handelModel = new SyncMethodHandlerModel();
				handelModel.setArgs(args);
				handelModel.setMethod(method);
				handelModel.setResult(result);
				
				//获取对应的回调类
				ProxyServiceMethodAnnotation annotation = method.getAnnotation(ProxyServiceMethodAnnotation.class);
				Class<? extends AsyncMethodHandler> handelBeanClass = annotation.handelBeanClass();
				String handelBeanName = annotation.handelBeanName();
				
				AsyncMethodHandler handelBean = null;
				//如果没有设置名字,就扫类型获取回调方法
				if(!StringUtils.isEmpty(handelBeanName)) {
					//如果有名字就按名字获取回调方法
					handelBean = (AsyncMethodHandler) applicationContext.getBean(handelBeanName);
				} else {
					try {
						handelBean = applicationContext.getBean(handelBeanClass);
					}
					catch(NoSuchBeanDefinitionException e) {
						try {
							handelBean = handelBeanClass.newInstance();
						} catch (InstantiationException | IllegalAccessException e1) {
							throw new NoClassDefFoundError(annotation.handelBeanClass().getName());
						}
					}
				}
				
				handelBean.handel(handelModel);
			}
		});
		new Thread().setDaemon(true);
	}
	/**
	 * 异步无需返回调用远程方法
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	@SuppressWarnings("unused")
	private void noAnswerInvokeRemoteMethod(Object proxy, Method method, Object[] args) {
		cachedThreadPool.execute(new Runnable() {
			@Override
			public void run() {
				syncInvokeRemoteMethod(proxy,method,args);
			}
		});
	}
	
	private ServiceHandler handler;
	/**
	 * 同步远程调用方法
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 */
	private Object syncInvokeRemoteMethod(Object proxy, Method method, Object[] args) {
		if(null == handler) {
			ProxyServiceAnnotation annotation = this.serviceinterface.getAnnotation(ProxyServiceAnnotation.class);
			String beanName = annotation.handelBeanName();
			if(!StringUtils.isEmpty(beanName)) {
				handler = (ServiceHandler) applicationContext.getBean(beanName);
			} else {
				try {
					handler = applicationContext.getBean(annotation.handelBeanClass());
				}
				catch(NoSuchBeanDefinitionException e) {
					try {
						handler = annotation.handelBeanClass().newInstance();
					} catch (InstantiationException | IllegalAccessException e1) {
						throw new NoClassDefFoundError(annotation.handelBeanClass().getName());
					}
				}
			}
		}
		return handler.handel(proxy, method, args);
	}
	
	/**
	 * 调用代理类上的方法
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
		final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
				.getDeclaredConstructor(Class.class, int.class);
		if (!constructor.isAccessible()) {
			constructor.setAccessible(true);
		}
		final Class<?> declaringClass = method.getDeclaringClass();
		return constructor
				.newInstance(declaringClass,
						MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE
								| MethodHandles.Lookup.PUBLIC)
				.unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
	}

	/**
	 * 判断是不是调用的代理类上的方法
	 */
	private boolean isDefaultMethod(Method method) {
		return (method.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
				&& method.getDeclaringClass().isInterface();
	}
}

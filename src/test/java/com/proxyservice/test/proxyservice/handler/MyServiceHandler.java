package com.proxyservice.test.proxyservice.handler;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.proxyservice.core.handler.ServiceHandler;


@Component
public class MyServiceHandler implements ServiceHandler {

	@Override
	public Object handel(Object proxy, Method method, Object[] args) {
		
		System.out.println("MyServiceHandler\t"+method.getName()+"\t"+(null == args? null : Arrays.asList(args)));
		return null;
	}

}

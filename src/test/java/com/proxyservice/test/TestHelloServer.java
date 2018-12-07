package com.proxyservice.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.proxyservice.test.proxyservice.HelloProxyService;



@Component
public class TestHelloServer {

	@Autowired
	private HelloProxyService proxyService;
	
	public void test ()
	{
		proxyService.syncHello("同步调用");
		proxyService.asyncHello("异步调用");
		proxyService.noanswerHello("不需返回异步调用");
	}
}

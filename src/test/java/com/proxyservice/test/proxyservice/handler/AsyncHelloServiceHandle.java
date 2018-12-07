package com.proxyservice.test.proxyservice.handler;


import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.proxyservice.core.handler.AsyncMethodHandler;
import com.proxyservice.core.handler.SyncMethodHandlerModel;


@Component
public class AsyncHelloServiceHandle implements AsyncMethodHandler {

	@Override
	public void handel(SyncMethodHandlerModel result) {
		
		System.out.println("AsyncHelloServiceHandle\t"+(null == result.getArgs()? null : Arrays.asList(result.getArgs())));
	}

}

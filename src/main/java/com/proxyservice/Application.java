package com.proxyservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.proxyservice.autoconfigure.ProxyServiceAutoConfiguration;

@ImportAutoConfiguration(ProxyServiceAutoConfiguration.class)
@SpringBootApplication
/**
 * 
 * @author liu_jianfu
 *
 */
public class Application {

	@Autowired
	public static void main(String[] args) {
		try
		{
			SpringApplication.run(Application.class, args);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

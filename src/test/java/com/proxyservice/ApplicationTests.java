package com.proxyservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.proxyservice.test.TestHelloServer;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Autowired
	private TestHelloServer ths;
	@Test
	public void contextLoads() {
		ths.test();
		ths.test();
	}

}

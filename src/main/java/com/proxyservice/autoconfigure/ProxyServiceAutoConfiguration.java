package com.proxyservice.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ProxyServiceScannerRegistrar.class)
/**
 * 
 * @author liu_jianfu
 *
 */
public class ProxyServiceAutoConfiguration {
}

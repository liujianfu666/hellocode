package com.proxyservice.autoconfigure;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import com.proxyservice.core.annotation.ProxyServiceAnnotation;
import com.proxyservice.core.proxy.ProxyServiceFactoryBean;
import com.proxyservice.core.scan.ProxyServiceScanner;
/**
 * @author liu_jianfu
 *
 */
public class ProxyServiceScannerRegistrar implements EnvironmentPostProcessor,ApplicationContextInitializer<ConfigurableApplicationContext>,
ImportBeanDefinitionRegistrar, ResourceLoaderAware {

	private static String mainApplicationClass;
	private static String configPackageStr;
	
	private ResourceLoader resourceLoader;

	private static ApplicationContext applicationContext;
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		ProxyServiceScanner scanner = new ProxyServiceScanner(registry,new ProxyServiceFactoryBean<Object>(applicationContext));
		try {
			if (this.resourceLoader != null) {
				scanner.setResourceLoader(this.resourceLoader);
			}
			//指定扫描的注解
			scanner.registerFilters(ProxyServiceAnnotation.class);
			List<String> packages = new ArrayList<>();
			packages.add(mainApplicationClass);
			if(!StringUtils.isEmpty(configPackageStr)) {
				packages.add(configPackageStr);
			}
			scanner.doScan(StringUtils.toStringArray(packages));
		} catch (IllegalStateException ex) {
		}
	}

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
		//当配置项加载完成后,获取配置的扫描包路径
		configPackageStr = environment.getProperty("proxyservice.package");
		mainApplicationClass = application.getMainApplicationClass().getPackage().getName();
	}

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		ProxyServiceScannerRegistrar.applicationContext = applicationContext;
	}
}

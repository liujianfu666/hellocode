package com.proxyservice.core.scan;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;


/**
 * 自定义扫描器,用来扫描带指定注解的类
 * @author liu_jianfu
 *
 */
public class ProxyServiceScanner extends ClassPathBeanDefinitionScanner {

	private List<String> annotationClassArray;// 需要扫描的注解类型

	//被扫描到的类的代理类
	private FactoryBean<?> factoryBean;

	public ProxyServiceScanner(BeanDefinitionRegistry registry,FactoryBean<?> factoryBean) {
		super(registry, false);
		this.annotationClassArray = new ArrayList<>();
		this.factoryBean = factoryBean;
	}

	/**
	 * 指定要扫描的注解
	 * @param annotationClass
	 */
	@SuppressWarnings("unchecked")
	public void registerFilters(Class<? extends Annotation>... annotationClass) {
		for(Class<? extends Annotation> tem:annotationClass) {
			//添加过滤条件
			addIncludeFilter(new AnnotationTypeFilter(tem));
			//记录对应的注解名称
			annotationClassArray.add(tem.getName());
		}
	}

	/**
	 * 开始扫描
	 */
	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		//开始扫描,并得到相应的结果
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			logger.warn("No remoteServiceInterface was found in '" + Arrays.toString(basePackages)
					+ "' package.");
		} else {
			//重新配置扫描到的bean
			processBeanDefinitions(beanDefinitions);
		}

		return beanDefinitions;
	}

	/**
	 * 重新配置扫描到的bean
	 * @param beanDefinitions
	 */
	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		for (BeanDefinitionHolder holder : beanDefinitions) {
			GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
			//指定构造方法,需传入一个接口类型的参数
			definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); // issue
			//指定该bean的类型为代理类型
			definition.setBeanClass(this.factoryBean.getClass());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		//如果这个bean对应的类上有指定的注解,就符合条件。
		Set<String> beanAnnotations = beanDefinition.getMetadata().getAnnotationTypes();
		for(String tmp:beanAnnotations) {
			if(annotationClassArray.contains(tmp)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		} else {
			logger.warn(
					"Skipping remoteService with name '" + beanName + "' and '" + beanDefinition.getBeanClassName()
							+ "' remoteServiceInterface. Bean already defined with the same name!");
			return false;
		}
	}
}

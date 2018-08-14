package com.gy.zeus.client;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * <p>Title: 获取spring上下文的对象实例</p>
 * <p>Description: 利用spring的BeanFactoryAware，将beanFactory保存在一个单例中</p>
 * <p>Company: letv.com</p>
 * 
    <bean id="serviceLocator" class="jmind.core.spring.SpringBeanLocator"
        factory-method="getInstance" />
 * 
 * @author 
 */
public class SpringBeanLocator implements BeanFactoryAware {

    private static final SpringBeanLocator SPRINGBEANLOCATOR = new SpringBeanLocator();

    public <T> T getBean(final Class<T> clazz) {
        return SPRINGBEANLOCATOR.getBeanFactory().getBean(clazz);
    }

    /**
    * 根据提供的bean名称得到相应的服务类    
    * @param beanName bean名称    
    */
    @SuppressWarnings("unchecked")
    public <T> T getBean(final String beanName) {
        return (T) SPRINGBEANLOCATOR.getBeanFactory().getBean(beanName);
    }

    /**
    * 根据提供的bean名称得到对应于指定类型的服务类
    * @param servName bean名称
    * @param clazz 返回的bean类型,若类型不匹配,将抛出异常
    */
    public <T> T getBean(final String beanName, final Class<T> clazz) {
        return SPRINGBEANLOCATOR.getBeanFactory().getBean(beanName, clazz);
    }

    public static SpringBeanLocator getInstance() {
        return SPRINGBEANLOCATOR;
    }

    private BeanFactory beanFactory;

    private SpringBeanLocator() {
    }

    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public void setBeanFactory(final BeanFactory factory) throws BeansException {

        this.beanFactory = factory;

    }
}

package com.example.learn.spring.a05;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;

import java.io.IOException;
import java.util.Set;

public class AtBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {
        try {
            CachingMetadataReaderFactory factory = new CachingMetadataReaderFactory();
            // 读取了Config.class的信息
            MetadataReader reader = factory.getMetadataReader(new ClassPathResource("com/example/learn/spring/a05/Config.class"));
            // 拿到信息里被@Bean注解修饰的方法信息
            Set<MethodMetadata> methods = reader.getAnnotationMetadata().getAnnotatedMethods(Bean.class.getName());
            for (MethodMetadata method : methods) {
                System.out.println(method);
                // 拿到@Bean注解的initMethod属性
                String initMethod = method.getAnnotationAttributes(Bean.class.getName()).get("initMethod").toString();
                // beanDefinition构造工具
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
                // 设置工厂方法名（@Bean生成的bean对象，均由bean工厂生成出来）
                builder.setFactoryMethodOnBean(method.getMethodName(), "config");
                // 设置自动装配模式，AUTOWIRE_CONSTRUCTOR 对于构造方法参数、工厂方法参数
                builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
                if (initMethod.length() > 0) {
                    builder.setInitMethodName(initMethod);
                }
                AbstractBeanDefinition bd = builder.getBeanDefinition();
                beanFactory.registerBeanDefinition(method.getMethodName(), bd);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

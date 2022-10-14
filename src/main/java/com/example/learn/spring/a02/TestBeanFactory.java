package com.example.learn.spring.a02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Resource;

public class TestBeanFactory {

    /**
     * SpringIOC在实现时，分为两个阶段：容器启动阶段、Bean实例化阶段
     * 容器启动阶段：加载配置、分析配置信息、装备到BeanDefinition放入容器、调用各个Bean工厂后处理器（BeanFactoryPostProcessor）
     * Bean实例化阶段：实例化Bean对象、设置Bean对象属性、检查Aware接口并设置相关依赖、BeanPostProcessor前置处理器、
     *               检查是否时InitializingBean来决定是否调用afterPropertiesSet方法、BeanPostProcessor后置处理器、
     *               检查是否配置自定义init-method、使用中、
     *               容器关闭销毁Bean时：检查是否实现DisposableBean来决定是否调用destroy方法、检查是否配置自定义destroy-method
     */
    public static void main(String[] args) {
        /* 容器启动阶段（这里跳过加载配置、分析配置信息阶段、直接使用DefaultListableBeanFactory，通过代码添加BeanDefinition） */
        // BeanFactory重要实现类：DefaultListableBeanFactory（XmlBeanFactory的父类）
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 向BeanFactory添加Bean定义
        // bean 的定义（class, scope, 初始化, 销毁）
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(Config.class).setScope("singleton").getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);

        // 遍历打印beanFactory中的bean定义
        System.out.println("遍历打印bean定义：");
        printDefinitionNames(beanFactory);
        // 上面只打印了config这一个bean，并没有解析Config上的@Configuration和@Bean注解，所以没有bean1和bean2
        // 解析@Configuration和@Bean注解，需要添加相应的Bean工厂后处理器（BeanFactoryPostProcessor）

        // 给 BeanFactory 添加一些常用的后处理器（依然没有解析@Configuration和@Bean注解，需要进了一步调用后处理器的方法）
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        System.out.println("遍历打印bean定义：");
        printDefinitionNames(beanFactory);
        /*
           一共添加5个后处理器（包括常用的BeanFactory后处理器和Bean后处理器）:
               internalConfigurationAnnotationProcessor   解析@Configuration和@Bean
               internalAutowiredAnnotationProcessor       解析@Autowired和@Value
               internalCommonAnnotationProcessor          解析@Resource
               internalEventListenerProcessor
               internalEventListenerFactory
        */

        // 调用Bean工厂后处理器，解析@Configuration和@Bean注解
        // BeanFactory 后处理器主要功能，补充了一些 bean 定义
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().forEach(beanFactoryPostProcessor -> {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        });
        System.out.println("遍历打印bean定义：");
        printDefinitionNames(beanFactory);

        /* Bean实例化阶段 */
        // 此时，通过getBean方法就能实例化Bean对象并返回。
        // 但是，@Autowired、@Resource等依赖注入的注解并没有解析，需要相关的Bean后处理器（BeanPostProcessor）

        // Bean 后处理器, 针对 bean 的生命周期的各个阶段提供扩展, 例如 @Autowired @Resource ...
        beanFactory.getBeansOfType(BeanPostProcessor.class).values().stream()
                .sorted(beanFactory.getDependencyComparator())  // 设置后处理顺序，根据其Order接口的getOrder方法
                .forEach(beanPostProcessor -> {
                    System.out.println(">>>>" + beanPostProcessor);
                    beanFactory.addBeanPostProcessor(beanPostProcessor);   // 添加，这样beanFactory在实例化Bean时，都会调用
                });

        beanFactory.preInstantiateSingletons(); // 准备好所有单例
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        System.out.println(beanFactory.getBean(Bean1.class).getBean2());
        System.out.println(beanFactory.getBean(Bean1.class).getInter());

        /*
            学到了什么:
            a. beanFactory 不会做的事
                   1. 不会主动调用 BeanFactory 后处理器
                   2. 不会主动添加 Bean 后处理器
                   3. 不会主动初始化单例
                   4. 不会解析beanFactory 还不会解析 ${ } 与 #{ }
            b. bean 后处理器会有排序的逻辑
         */

        System.out.println("Common:" + (Ordered.LOWEST_PRECEDENCE - 3));
        System.out.println("Autowired:" + (Ordered.LOWEST_PRECEDENCE - 2));
    }

    static void printDefinitionNames(DefaultListableBeanFactory beanFactory) {
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }

        @Bean
        public Bean3 bean3() {
            return new Bean3();
        }

        @Bean
        public Bean4 bean4() {
            return new Bean4();
        }
    }

    interface Inter {

    }

    static class Bean3 implements Inter {

    }

    static class Bean4 implements Inter {

    }

    static class Bean1 {
        private static final Logger log = LoggerFactory.getLogger(Bean1.class);

        public Bean1() {
            log.debug("构造 Bean1()");
        }

        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2() {
            return bean2;
        }

        @Autowired  // Autowired特性：如果两个类型相同Bean，会优先根据名称注入
        @Resource(name = "bean4")
        private Inter bean3;

        public Inter getInter() {
            return bean3;
        }
    }

    static class Bean2 {
        private static final Logger log = LoggerFactory.getLogger(Bean2.class);

        public Bean2() {
            log.debug("构造 Bean2()");
        }
    }

}

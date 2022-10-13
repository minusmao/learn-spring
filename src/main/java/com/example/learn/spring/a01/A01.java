package com.example.learn.spring.a01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;

/*
    BeanFactory 与 ApplicationContext 的区别
 */
@SpringBootApplication
public class A01 {

    private static final Logger log = LoggerFactory.getLogger(A01.class);

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, IOException {

        ConfigurableApplicationContext context = SpringApplication.run(A01.class, args);
        /*
            1. 到底什么是 BeanFactory
                - 它是 ApplicationContext 的父接口
                - 它才是 Spring 的核心容器, 主要的 ApplicationContext 实现都【组合】了它的功能
         */

        // Ctrl+左键：跳转到定义（上级接口定义的地方）
        // Ctrl+Alt+左键：跳转到实现（当前实现）
        // 当前getBean方法的实现在AbstractApplicationContext中，其组合了BeanFactory，最终调用了BeanFactory的getBean方法
        context.getBean("a01");
        System.out.println(context);

        // Ctrl+F12：查看接口的所有方法
        // BeanFactory的重要实现类：DefaultListableBeanFactory
        // DefaultListableBeanFactory实现了：BeanFactory，间接继承了DefaultSingletonBeanRegistry（有个singletonObjects的map容器，用来存储单列bean）

        /*
            2. BeanFactory 能干点啥
                - 表面上只有 getBean
                - 实际上控制反转、基本的依赖注入、直至 Bean 的生命周期的各种功能, 都由它的实现类提供
         */
        Field singletonObjects = DefaultSingletonBeanRegistry.class.getDeclaredField("singletonObjects");
        singletonObjects.setAccessible(true);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        // 反射拿出singletonObjects，并遍历其中的bean
        Map<String, Object> map = (Map<String, Object>) singletonObjects.get(beanFactory);
        map.entrySet().stream().filter(e -> e.getKey().startsWith("component"))
                .forEach(e -> System.out.println(e.getKey() + "=" + e.getValue()));

        /*
            3. ApplicationContext 比 BeanFactory 多点啥
         */
        // ApplicationContext实现了MessageSource接口：处理国际化资源
        // 国际化messages文件默认放在classpath下，可以在配置文件中配置其他位置：spring.messages.basename=i18n/messages
        System.out.println(context.getMessage("hi", null, Locale.CHINESE));
        System.out.println(context.getMessage("hi", null, Locale.ENGLISH));
        System.out.println(context.getMessage("hi", null, Locale.JAPANESE));

        // ApplicationContext实现了ResourcePatternResolver接口：通配符匹配资源（磁盘路径或类路径的一些文件）
        Resource[] resources = context.getResources("classpath*:META-INF/spring.factories");// 加*号会访问所有jar包的内容
        for (Resource resource : resources) {
            System.out.println(resource);
        }

        // ApplicationContext实现了EnvironmentCapable接口：读取spring环境信息（环境变量、application.yml和application.properties文件中的配置信息）
        System.out.println(context.getEnvironment().getProperty("java_home"));
        System.out.println(context.getEnvironment().getProperty("server.port"));

        // ApplicationContext实现了ApplicationEventPublisher接口：发布事件
        context.publishEvent(new UserRegisteredEvent(context));
        context.getBean(Component1.class).register();

        /*
            4. 学到了什么
                a. BeanFactory 与 ApplicationContext 并不仅仅是简单接口继承的关系, ApplicationContext 组合并扩展了 BeanFactory 的功能
                b. 又新学一种代码之间解耦途径
            练习：完成用户注册与发送短信之间的解耦, 用事件方式、和 AOP 方式分别实现
         */
    }
}

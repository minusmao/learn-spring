package com.example.learn.spring.a44;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/*
    做这个试验前, 先在 target/classes 创建 META-INF/spring.components, 内容为

    com.example.learn.spring.a44.Bean1=org.springframework.stereotype.Component
    com.example.learn.spring.a44.Bean2=org.springframework.stereotype.Component

    做完实现建议删除, 避免影响其它组件扫描的结果

    真实项目中, 这个步骤可以自动完成, 加入以下依赖
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context-indexer</artifactId>
        <optional>true</optional>
    </dependency>
    并在需要被扫描的bean上加上@Indexed注解，这样在编译时就根据 @Indexed 生成 META-INF/spring.components 文件
    @Component默认加了@Indexed注解，如果自定义了spring.components文件，则以自定义为准
 */
public class A44 {
    public static void main(String[] args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 组件扫描的核心类
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);

        scanner.scan(A44.class.getPackageName());

        // 发现只有spring.components中配置的bean被扫描到
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        /*
            学到了什么
                a. @Indexed 的原理, 在编译时就根据 @Indexed 生成 META-INF/spring.components 文件
                扫描时
                1. 如果发现 META-INF/spring.components 存在, 以它为准加载 bean definition
                2. 否则, 会遍历包下所有 class 资源 (包括 jar 内的)
         */
    }
}

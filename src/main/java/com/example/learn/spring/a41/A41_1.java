package com.example.learn.spring.a41;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * 自动配置原理
 */
public class A41_1 {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.getDefaultListableBeanFactory().setAllowBeanDefinitionOverriding(false);  // 设置bean不允许被覆盖
        context.registerBean("config", Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);
        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }
    }

    @Configuration // 本项目的配置类
//    @Import({AutoConfiguration1.class, AutoConfiguration2.class})  // 导入第三方配置类
    @Import(MyImportSelector.class)  // 通过扫描器，导入第三方配置
    static class Config {
        @Bean
        public Bean1 bean1() {
            // 补充
            // ImportSelector接口：会先注册@Import里的bean对象，再注册自身的bean对象，如果有相同的bean，默认会被后注册的覆盖，spring-boot中设置了bean不允许被覆盖
            // DeferredImportSelector接口：会先注册自身的bean对象，再注册@Import里的bean对象
            return new Bean1("本项目");
        }
    }

    // 配合@Import注解的扫描器
    static class MyImportSelector implements DeferredImportSelector {
        // 该方法的返回值返回需要导入的配置类名
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            // 方式1：将需要导入的配置类名写死在代码中
//            return new String[] {AutoConfiguration1.class.getName(), AutoConfiguration2.class.getName()};

            // 方式2：将需要导入的配置类名写在外部配置文件（META-INF/spring.factories）中，通过SpringFactoriesLoader加载（会加载当前项目和所有jar包）
            // spring-boot使用的是EnableAutoConfiguration作为配置文件中的键
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            for (String name : SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class, null)) {
                System.out.println(name);
            }
            // 此处我们扫描配置文件中的MyImportSelector键
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            List<String> names = SpringFactoriesLoader.loadFactoryNames(MyImportSelector.class, null);
            return names.toArray(new String[0]);
        }
    }


    @Configuration // 第三方的配置类
    static class AutoConfiguration1 {
        @Bean
        @ConditionalOnMissingBean  // 条件注册bean：当缺失此bean时生效
        public Bean1 bean1() {
            return new Bean1("第三方");
        }
    }

    static class Bean1 {
        private String name;

        public Bean1() {
        }

        public Bean1(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Bean1{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Configuration // 第三方的配置类
    static class AutoConfiguration2 {
        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    static class Bean2 {

    }

}

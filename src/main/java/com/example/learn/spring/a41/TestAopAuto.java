package com.example.learn.spring.a41;

import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

public class TestAopAuto {
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        // 创建Environment方便自己写入一些配置信息
        StandardEnvironment env = new StandardEnvironment();
//        env.getPropertySources().addLast(new SimpleCommandLinePropertySource("--spring.aop.auto=false"));
        context.setEnvironment(env);
        // 添加后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        // 注册bean
        context.registerBean(Config.class);
        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        System.out.println(">>>>>>>>>>>>>>>");
        // 查看AopAutoConfiguration中注册的AnnotationAwareAspectJAutoProxyCreator
        // AnnotationAwareAspectJAutoProxyCreator的作用是创建代理类
        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(
                "org.springframework.aop.config.internalAutoProxyCreator", AnnotationAwareAspectJAutoProxyCreator.class);
        System.out.println(creator.isProxyTargetClass());

    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AopAutoConfiguration.class.getName()};  // 导入aop配置

            /*
              会多出以下4个bean
                org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$AspectJAutoProxyingConfiguration$CglibAutoProxyConfiguration
                org.springframework.aop.config.internalAutoProxyCreator
                org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$AspectJAutoProxyingConfiguration
                org.springframework.boot.autoconfigure.aop.AopAutoConfiguration
             */

            /*
              AopAutoConfiguration中的@Conditional配置注解分析：
                @ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
                    在配置文件中找以"spring.aop"为键前缀，以"auto"为键名，以"true"为键值的配置信息，满足则通过
                    matchIfMissing = true  表示没有这个配置信息，也通过
                @ConditionalOnClass(Advice.class)
                    检查类路径下是否存在Advice类，存在则通过
                @ConditionalOnMissingClass("org.aspectj.weaver.Advice")
                    检查类路径下是否缺失"org.aspectj.weaver.Advice"类，缺失则通过

              AopAutoConfiguration中的@EnableAspectJAutoProxy注解分析：
                一般以@Enable开头的注解，其内部就是通过@Import导入其他配置类
                该注解 @Import(AspectJAutoProxyRegistrar.class)
                AspectJAutoProxyRegistrar继承了ImportBeanDefinitionRegistrar（该Registrar作用：通过编程的方式注册bean）
            */
        }
    }
}

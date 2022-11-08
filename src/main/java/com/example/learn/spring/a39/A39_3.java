package com.example.learn.spring.a39;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.*;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Set;

/*
Boot 启动过程
    阶段一：SpringApplication 构造
        1. 记录 BeanDefinition 源
        2. 推断应用类型
        3. 记录 ApplicationContext 初始化器
        4. 记录监听器
        5. 推断主启动类

    阶段二：执行 run 方法（12个步骤，7个事件）
        1. 得到 SpringApplicationRunListeners，名字取得不好，实际是事件发布器
           * 发布 application starting 事件1️⃣
        2. 封装启动 args
        3. 准备 Environment 添加命令行参数（*）
        4. ConfigurationPropertySources 处理（*）  // 统一Environment参数获取，包括支持下划线、减号、驼峰规则
           * 发布 application environment 已准备事件2️⃣
        5. 通过 EnvironmentPostProcessorApplicationListener 进行 env 后处理（*）  // 通过事件监听器执行Environment后处理器增强
           * application.properties，由 StandardConfigDataLocationResolver 解析
           * spring.application.json
        6. 绑定 spring.main 到 SpringApplication 对象（*）  // 前提知识：@ConfigurationProperties的功能实现，通过Binder将env的数据绑定到对象上
        7. 打印 banner（*）
        8. 创建容器
        9. 准备容器
           * 发布 application context 已初始化事件3️⃣
        10. 加载 bean 定义
            * 发布 application prepared 事件4️⃣
        11. refresh 容器
            * 发布 application started 事件5️⃣
        12. 执行 runner
            * 发布 application ready 事件6️⃣
            * 这其中有异常，发布 application failed 事件7️⃣
 */

/*
    步骤1在A39_2
    步骤2，步骤8-12在A39_3
    步骤3在org.springframework.boot包下
 */

// 运行时请添加运行参数 --server.port=8080 debug
public class A39_3 {
    @SuppressWarnings("all")
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication();
        app.addInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext) {
                System.out.println("执行初始化器增强...");
            }
        });

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> 2. 封装启动 args");
        DefaultApplicationArguments arguments = new DefaultApplicationArguments(args);// 封装参数，供实现了ApplicationRunner的bean的run()方法使用

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> 8. 创建容器");
        GenericApplicationContext context = createApplicationContext(WebApplicationType.SERVLET); // 根据推断的容器类型，创建指定容器

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> 9. 准备容器");  // 执行之前准备的容器初始化器
        for (ApplicationContextInitializer initializer : app.getInitializers()) {
            initializer.initialize(context);
        }

        // 从各种来源加载bean
        // 源码SpringApplication对象通过getAllSources()方法拿到的primarySources和sources
        // 其中primarySources在构造时传入，而sources则可以通过setSources()方法设置
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> 10. 加载 bean 定义");
        DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader reader1 = new AnnotatedBeanDefinitionReader(beanFactory);
        XmlBeanDefinitionReader reader2 = new XmlBeanDefinitionReader(beanFactory);
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);

        reader1.register(Config.class);  // 真正开始加载bean
        reader2.loadBeanDefinitions(new ClassPathResource("b03.xml"));
        scanner.scan("com.example.learn.spring.a39.sub");

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> 11. refresh 容器");
        context.refresh();

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println("name:" + name + " 来源：" + beanFactory.getBeanDefinition(name).getResourceDescription());
        }

        // 执行实现了特定接口（CommandLineRunner、ApplicationRunner）的bean的run()方法
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> 12. 执行 runner");
        for (CommandLineRunner runner : context.getBeansOfType(CommandLineRunner.class).values()) {
            runner.run(args);
        }
        // ApplicationRunner的需要封装后的参数对象，由第2步封装
        for (ApplicationRunner runner : context.getBeansOfType(ApplicationRunner.class).values()) {
            runner.run(arguments);
        }

        /*
            学到了什么
            a. 创建容器、加载 bean 定义、refresh, 对应的步骤

         */
    }

    /**
     * 根据推断的容器类型，创建指定容器
     */
    private static GenericApplicationContext createApplicationContext(WebApplicationType type) {
        GenericApplicationContext context = null;
        switch (type) {
            case SERVLET -> context = new AnnotationConfigServletWebServerApplicationContext();
            case REACTIVE -> context = new AnnotationConfigReactiveWebServerApplicationContext();
            case NONE -> context = new AnnotationConfigApplicationContext();
        }
        return context;
    }

    static class Bean4 {

    }

    static class Bean5 {

    }

    static class Bean6 {

    }

    @Configuration
    static class Config {
        @Bean
        public Bean5 bean5() {
            return new Bean5();
        }

        @Bean
        public ServletWebServerFactory servletWebServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public CommandLineRunner commandLineRunner() {
            return new CommandLineRunner() {
                @Override
                public void run(String... args) throws Exception {
                    System.out.println("commandLineRunner()..." + Arrays.toString(args));
                }
            };
        }

        @Bean
        public ApplicationRunner applicationRunner() {
            return new ApplicationRunner() {
                @Override
                public void run(ApplicationArguments args) throws Exception {
                    System.out.println("applicationRunner()..." + Arrays.toString(args.getSourceArgs()));
                    System.out.println(args.getOptionNames());  // 带 -- 的参数
                    System.out.println(args.getOptionValues("server.port"));
                    System.out.println(args.getNonOptionArgs());  // 不带 -- 的参数
                }
            };
        }
    }
}

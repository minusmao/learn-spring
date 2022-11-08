package com.example.learn.spring.a39;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Method;
import java.util.Set;

@Configuration
public class A39_1 {

    public static void main(String[] args) throws Exception {
        /*
          spring-boot项目启动：
              SpringApplication.run(A39_1.class, args);
          该方法最终执行到：return new SpringApplication(primarySources).run(args);
                        先调用构造方法创建SpringApplication对象，再调用对象的run()方法创建spring容器
                        构造方法做准备工作，对象的run()方法才会创建spring容器
                        构造方法分为5个准备工作，run()方法分为12个步骤
        */
        /*
          模拟spring-boot项目启动：构造方法部分
              public SpringApplication(ResourceLoader resourceLoader, Class<?>... primarySources) {
                  this.resourceLoader = resourceLoader;
                  Assert.notNull(primarySources, "PrimarySources must not be null");
                  this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));   // 1. 获取BeanDefinition源
                  this.webApplicationType = WebApplicationType.deduceFromClasspath();  // 2. 推断应用类型
                  this.bootstrapRegistryInitializers = new ArrayList<>(
                          getSpringFactoriesInstances(BootstrapRegistryInitializer.class));

                  // 3. ApplicationContext初始化器（此处是读取了配置文件中的初始化器，ApplicationContextInitializer表示初始化器类）
                  setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class));
                  // 4. 监听器与事件（此处是读取了配置文件中的事件监听器）
                  setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));
                  // 5. 演示主类推断
                  this.mainApplicationClass = deduceMainApplicationClass();
              }
         */
        System.out.println("1. 演示获取 Bean Definition 源");
        SpringApplication spring = new SpringApplication(A39_1.class);  // @Configuration注解形式的源
        spring.setSources(Set.of("classpath:b01.xml"));  // xml文件形式的源

        System.out.println("2. 演示推断应用类型");
        Method deduceFromClasspath = WebApplicationType.class.getDeclaredMethod("deduceFromClasspath");
        deduceFromClasspath.setAccessible(true);
        System.out.println("\t应用类型为:" + deduceFromClasspath.invoke(null));

        System.out.println("3. 演示 ApplicationContext 初始化器");  // 作用：对ApplicationContext的功能做一些扩展，初始化器会在run()时被调用
//        spring.addInitializers(applicationContext -> {
//            if (applicationContext instanceof GenericApplicationContext gac) {
//                gac.registerBean("bean3", Bean3.class);
//            }
//        });
        spring.addInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext) {
                if (applicationContext instanceof GenericApplicationContext gac) {
                    gac.registerBean("bean3", Bean3.class);
                }
            }
        });

        System.out.println("4. 演示监听器与事件");
//        spring.addListeners(event -> System.out.println("\t事件为:" + event.getClass()));
        spring.addListeners(new ApplicationListener<ApplicationEvent>() {
            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                System.out.println("\t事件为:" + event.getClass());
            }
        });

        System.out.println("5. 演示主类推断");  // 查看main方法所在的类
        Method deduceMainApplicationClass = SpringApplication.class.getDeclaredMethod("deduceMainApplicationClass");
        deduceMainApplicationClass.setAccessible(true);
        System.out.println("\t主类是：" + deduceMainApplicationClass.invoke(spring));


        ConfigurableApplicationContext context = spring.run(args);
        // run()方法会做以下内容：
        // 创建 ApplicationContext
        // 调用初始化器 对 ApplicationContext 做扩展
        // ApplicationContext.refresh

        for (String name : context.getBeanDefinitionNames()) {
            System.out.println("name: " + name + " 来源：" + context.getBeanFactory().getBeanDefinition(name).getResourceDescription());
            // 小知识：context.getBeanFactory().getBeanDefinition(name).getResourceDescription()方法可以查看bean的来源
        }
        context.close();
        /*
            学到了什么
            a. SpringApplication 构造方法中所做的操作
                1. 可以有多种源用来加载 bean 定义
                2. 应用类型推断
                3. 容器初始化器
                4. 演示启动各阶段事件
                5. 演示主类推断
         */
    }

    static class Bean1 {

    }

    static class Bean2 {

    }

    static class Bean3 {

    }

    @Bean
    public Bean2 bean2() {
        return new Bean2();
    }

    @Bean
    public TomcatServletWebServerFactory servletWebServerFactory() {
        return new TomcatServletWebServerFactory();
    }
}

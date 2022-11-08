package org.springframework.boot;

import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.event.EventPublishingRunListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.boot.logging.DeferredLogs;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import javax.swing.*;
import java.util.List;

/*
    可以添加参数 --spring.application.json={\"server\":{\"port\":9090}} 测试 SpringApplicationJsonEnvironmentPostProcessor
 */

/**
 * spring-boot的run()方法步骤5：通过 EnvironmentPostProcessorApplicationListener 进行 env 后处理（*）
 * 源码中添加配置文件来源在步骤5
 * application.properties，由 StandardConfigDataLocationResolver 解析
 */
public class A39Step5 {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication();
        app.addListeners(new EnvironmentPostProcessorApplicationListener());  // Environment后处理器的功能由此监听器调用

        /*
           所有的后处理器在配置文件中
              # Environment Post Processors
                org.springframework.boot.env.EnvironmentPostProcessor=\
                org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
                org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor,\
                org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor,\
                org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor,\
                org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor,\
                org.springframework.boot.reactor.DebugAgentEnvironmentPostProcessor
        */
        /*List<String> names = SpringFactoriesLoader.loadFactoryNames(EnvironmentPostProcessor.class, Step5.class.getClassLoader());
        for (String name : names) {
            System.out.println(name);
        }*/

        EventPublishingRunListener publisher = new EventPublishingRunListener(app, args);
        ApplicationEnvironment env = new ApplicationEnvironment();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> 增强前");
        for (PropertySource<?> ps : env.getPropertySources()) {
            System.out.println(ps);
        }
        publisher.environmentPrepared(new DefaultBootstrapContext(), env);   // 告诉监听器执行Environment后处理器增强
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> 增强后");
        for (PropertySource<?> ps : env.getPropertySources()) {
            System.out.println(ps);
        }

    }

    private static void test1() {
        SpringApplication app = new SpringApplication();
        ApplicationEnvironment env = new ApplicationEnvironment();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> 增强前");
        for (PropertySource<?> ps : env.getPropertySources()) {
            System.out.println(ps);
        }

        /*
           所有的后处理器在配置文件中
              # Environment Post Processors
                org.springframework.boot.env.EnvironmentPostProcessor=\
                org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor,\
                org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor,\
                org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor,\
                org.springframework.boot.env.SpringApplicationJsonEnvironmentPostProcessor,\
                org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor,\
                org.springframework.boot.reactor.DebugAgentEnvironmentPostProcessor
        */
        // 后处理器方式增加Environment源
        ConfigDataEnvironmentPostProcessor postProcessor1 = new ConfigDataEnvironmentPostProcessor(new DeferredLogs(), new DefaultBootstrapContext());
        postProcessor1.postProcessEnvironment(env, app);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> 增强后");
        for (PropertySource<?> ps : env.getPropertySources()) {
            System.out.println(ps);
        }
        RandomValuePropertySourceEnvironmentPostProcessor postProcessor2 = new RandomValuePropertySourceEnvironmentPostProcessor(new DeferredLog());
        postProcessor2.postProcessEnvironment(env, app);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>> 增强后");
        for (PropertySource<?> ps : env.getPropertySources()) {
            System.out.println(ps);
        }
        System.out.println(env.getProperty("server.port"));
        System.out.println(env.getProperty("random.int"));
        System.out.println(env.getProperty("random.int"));
        System.out.println(env.getProperty("random.int"));
        System.out.println(env.getProperty("random.uuid"));
        System.out.println(env.getProperty("random.uuid"));
        System.out.println(env.getProperty("random.uuid"));
    }
}

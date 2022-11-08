package com.example.learn.spring.a39;

import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.lang.reflect.Constructor;
import java.util.List;

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
        4. ConfigurationPropertySources 处理（*）
           * 发布 application environment 已准备事件2️⃣
        5. 通过 EnvironmentPostProcessorApplicationListener 进行 env 后处理（*）
           * application.properties，由 StandardConfigDataLocationResolver 解析
           * spring.application.json
        6. 绑定 spring.main 到 SpringApplication 对象（*）
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

public class A39_2 {
    public static void main(String[] args) throws Exception{

        // 添加 app 监听器
        SpringApplication app = new SpringApplication();
        app.addListeners(e -> System.out.println(e.getClass()));

        /*
            SpringApplicationRunListener事件发布器接口 只有一个实现类 EventPublishingRunListener
            关系在配置文件中写好:
                org.springframework.boot.SpringApplicationRunListener=\
                org.springframework.boot.context.event.EventPublishingRunListener
         */

        // 获取事件发送器实现类名（只有一个类名，即EventPublishingRunListener）
        List<String> names = SpringFactoriesLoader.loadFactoryNames(SpringApplicationRunListener.class, A39_2.class.getClassLoader());
        for (String name : names) {
            System.out.println(name);
            Class<?> clazz = Class.forName(name);
            Constructor<?> constructor = clazz.getConstructor(SpringApplication.class, String[].class);
            // 事件发布器
            SpringApplicationRunListener publisher = (SpringApplicationRunListener) constructor.newInstance(app, args);

            // 发布事件（7个）
            DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
            publisher.starting(bootstrapContext); // spring boot 开始启动
            publisher.environmentPrepared(bootstrapContext, new StandardEnvironment()); // 环境信息准备完毕
            GenericApplicationContext context = new GenericApplicationContext();
            publisher.contextPrepared(context); // 在 spring 容器创建，并调用初始化器之后，发送此事件
            publisher.contextLoaded(context); // 所有 bean definition 加载完毕
            context.refresh();
            publisher.started(context); // spring 容器初始化完成(refresh 方法调用完毕)
            publisher.running(context); // spring boot 启动完毕

            publisher.failed(context, new Exception("出错了")); // spring boot 启动出错
        }

        /*
            学到了什么
            a. 如何读取 spring.factories 中的配置
            b. run 方法内获取事件发布器 (得到 SpringApplicationRunListeners) 的过程, 对应步骤中
                1.获取事件发布器
                发布 application starting 事件1️⃣
                发布 application environment 已准备事件2️⃣
                发布 application context 已初始化事件3️⃣
                发布 application prepared 事件4️⃣
                发布 application started 事件5️⃣
                发布 application ready 事件6️⃣
                这其中有异常，发布 application failed 事件7️⃣
         */
    }


}

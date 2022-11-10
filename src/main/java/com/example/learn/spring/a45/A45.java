package com.example.learn.spring.a45;

import org.springframework.aop.framework.Advised;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;

@SpringBootApplication
public class A45 {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(A45.class, args);

        Bean1 proxy = context.getBean(Bean1.class);



        /*
            1.演示 spring 代理的设计特点
                依赖注入和初始化影响的是原始对象
                 （补充：可以看a03里的MyBeanPostProcessor，负责生成代理的方法postProcessAfterInitialization()最后才会执行，
                       而负责依赖注入的postProcessProperties()、负责@PostConstruct的postProcessBeforeInitialization()会先执行）
                代理与目标是两个对象，二者成员变量并不共用数据（即代理对象的属性和目标对象的属性值不同）
                 （补充：一般访问成员变量都是通过getter方法，该方法会拿到目标对象的属性，所以察觉不出问题）
         */
        showProxyAndTarget(proxy);

        System.out.println(">>>>>>>>>>>>>>>>>>>");
        System.out.println(proxy.getBean2());  // 会拿到目标对象的bean2属性
        System.out.println(proxy.bean2);  // 会拿到代理对象的bean2属性
        System.out.println(proxy.isInitialized());

        /*
            2.演示 static 方法、final 方法、private 方法均无法增强（代理）
              代理只能代理能够重写的方法
         */

        proxy.m1();
        proxy.m2();
        proxy.m3();
        Method m4 = Bean1.class.getDeclaredMethod("m4");
        m4.setAccessible(true);
        m4.invoke(proxy);

        context.close();
    }


    public static void showProxyAndTarget(Bean1 proxy) throws Exception {
        System.out.println(">>>>> 代理中的成员变量");
        System.out.println("\tinitialized=" + proxy.initialized);
        System.out.println("\tbean2=" + proxy.bean2);

        if (proxy instanceof Advised advised) {  // 笔记：拿到目标对象的方式，将代理对象转换为Advised接口，再调用advised.getTargetSource().getTarget()
            System.out.println(">>>>> 目标中的成员变量");
            Bean1 target = (Bean1) advised.getTargetSource().getTarget();
            System.out.println("\tinitialized=" + target.initialized);
            System.out.println("\tbean2=" + target.bean2);
        }
    }

}

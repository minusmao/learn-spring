package com.example.learn.spring.a09;

import com.example.learn.spring.a09.service.MyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 不通过代理类实现AOP方式1-ajc编译器：
 *     AspectJ通过改变编译的字节码实现AOP（前提：需要maven引入aspectj-maven-plugin插件，且由maven编译）
 */
@SpringBootApplication
public class A09 {

    private static final Logger log = LoggerFactory.getLogger(A09.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(A09.class);
        MyService service = context.getBean(MyService.class);

        // 查看类型，发现并不是代理类
        log.debug("service class: {}", service.getClass());
        // 执行，发现已有代理功能
        service.objectFoo();
        // **并且：类方法也可以实现AOP功能
        MyService.foo();

        context.close();

        // **并且：该AOP不是spring用代理类实现的，所以不依赖spring容器
        new MyService().objectFoo();

        /*
            学到了什么
            1. aop 的原理并非代理一种, 编译器也能玩出花样
         */
    }

}

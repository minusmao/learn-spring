package com.example.learn.spring.a08.sub;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class E {

    // 单例注入多例时，只会注入一次，多例效果会失效
    // 解决多例失效1：加@Lazy注解，会注入代理对象，调用时，由代理对象生成多例
    @Lazy
    @Autowired
    private F1 f1;

    // 解决多例失效2：多例Bean设置proxyMode
    @Autowired
    private F2 f2;

    // 解决多例失效3：注入对象工厂
    @Autowired
    private ObjectFactory<F3> f3;

    // 解决多例失效4：通过ApplicationContext获取Bean
    @Autowired
    private ApplicationContext context;

    public F1 getF1() {
        return f1;
    }

    public F2 getF2() {
        return f2;
    }

    public F3 getF3() {
        return f3.getObject();
    }

    public F4 getF4() {
        return context.getBean(F4.class);
    }
}

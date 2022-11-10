package com.example.learn.spring.a45;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Bean1 {

    private static final Logger log = LoggerFactory.getLogger(Bean1.class);

    protected Bean2 bean2;

    protected boolean initialized;

    @Autowired  // 依赖注入是直接操作的原始对象
    public void setBean2(Bean2 bean2) {
        log.debug("setBean2(Bean2 bean2)");
        this.bean2 = bean2;
    }

    @PostConstruct  // 这里也是直接操作的原始对象
    public void init() {
        log.debug("init");
        initialized = true;
    }

    public Bean2 getBean2() {
        log.debug("getBean2()");
        return bean2;
    }

    public boolean isInitialized() {
        log.debug("isInitialized()");
        return initialized;
    }

    public void m1() {
        System.out.println("m1() 成员方法：会被代理增强");
    }

    final public void m2() {
        System.out.println("m2() final 方法：不会被代理增强");
    }

    static public void m3() {
        System.out.println("m3() static 方法：不会被代理增强");
    }

    private void m4() {
        System.out.println("m4() private 方法：不会被代理增强");
    }

}

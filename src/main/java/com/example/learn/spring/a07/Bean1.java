package com.example.learn.spring.a07;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;

public class Bean1 implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(Bean1.class);

    // 先执行Bean后处理器的初始化
    @PostConstruct
    public void init1() {
        log.debug("初始化1");
    }

    // Aware接口的方法执行顺序是在上下两者之间

    // 再执行InitializationBean接口的初始化方法
    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("初始化2");
    }

    // 最后执行配置的初始化方法
    public void init3() {
        log.debug("初始化3");
    }
}

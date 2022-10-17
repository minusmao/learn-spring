package com.example.learn.spring.a08;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Scope("application")
@Component
public class BeanForApplication {
    private static final Logger log = LoggerFactory.getLogger(BeanForApplication.class);

    // 代码实际运行时，停止服务时，并不会调用application域的destroy方法
    @PreDestroy
    public void destroy() {
        log.debug("destroy");
    }
}

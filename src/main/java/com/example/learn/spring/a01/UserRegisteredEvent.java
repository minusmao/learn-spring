package com.example.learn.spring.a01;

import org.springframework.context.ApplicationEvent;

/**
 * 自定义事件，继承ApplicationEvent
 */
public class UserRegisteredEvent extends ApplicationEvent {
    /**
     * 构造方法
     * @param source 事件来源
     */
    public UserRegisteredEvent(Object source) {
        super(source);
    }
}

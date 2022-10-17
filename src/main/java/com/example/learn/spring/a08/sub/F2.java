package com.example.learn.spring.a08.sub;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

// 解决单例注入多例，多例失效问题：除了在注入的地方加@Lazy之外，还可以设置多例Bean的proxyMode
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class F2 {
}

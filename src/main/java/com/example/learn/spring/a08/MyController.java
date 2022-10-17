package com.example.learn.spring.a08;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class MyController {

    // 单例注入多例时，只会注入一次，多例效果会失效
    // 加@Lazy注解，会注入代理对象，调用时，由代理对象生成多例
    @Lazy
    @Autowired
    private BeanForRequest beanForRequest;

    @Lazy
    @Autowired
    private BeanForSession beanForSession;

    @Lazy
    @Autowired
    private BeanForApplication beanForApplication;

    @GetMapping(value = "/test", produces = "text/html")
    public String test(HttpServletRequest request, HttpSession session) {
        ServletContext sc = request.getServletContext();
        String sb = "<ul>" +
                    "<li>" + "request scope:" + beanForRequest + "</li>" +
                    "<li>" + "session scope:" + beanForSession + "</li>" +
                    "<li>" + "application scope:" + beanForApplication + "</li>" +
                    "</ul>";

        // 并非原类，而是代理类：com.example.learn.spring.a08.BeanForRequest$$EnhancerBySpringCGLIB$$424eed14
        System.out.println(beanForRequest.getClass());
        System.out.println(beanForSession.getClass());
        System.out.println(beanForApplication.getClass());

        return sb;
    }

}

package com.example.learn.spring.a20;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {
    // 将父类的invokeHandlerMethod访问修饰符改为public，方便调用
    // 该方法负责处理从HandlerMethod，最终调用匹配到的controller控制的方法
    // 此外HandlerAdapter还要调用：参数处理器和返回值处理器
    @Override
    public ModelAndView invokeHandlerMethod(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        return super.invokeHandlerMethod(request, response, handlerMethod);
    }
}

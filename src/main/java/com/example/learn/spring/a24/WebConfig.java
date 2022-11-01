package com.example.learn.spring.a24;

import com.example.learn.spring.a23.MyDateFormatter;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

@Configuration
public class WebConfig {

    @ControllerAdvice  // 控制器增强
    static class MyControllerAdvice {
        @InitBinder  // 添加自定义参数解析器
        public void binder3(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDateFormatter("binder3 转换器"));
        }

//        @ExceptionHandler    // 控制器异常统一处理

//        @ModelAttribute  // 具体用法参考：http://c.biancheng.net/spring_mvc/model-attribute.html
        // @ModelAttribute修饰再方法上：该方法会在controller控制器方法之前执行，生成一个ModelAndView，存放了一些值，后面的controller方法可以拿到里面的值
        // @ModelAttribute修饰再方法参数上：会将表单数据解析到该参数（对象）中，在A21的162行有使用
    }

    @Controller
    static class Controller1 {
        @InitBinder
        public void binder1(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDateFormatter("binder1 转换器"));
        }

        public void foo() {

        }
    }

    @Controller
    static class Controller2 {
        @InitBinder
        public void binder21(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDateFormatter("binder21 转换器"));
        }

        @InitBinder
        public void binder22(WebDataBinder webDataBinder) {
            webDataBinder.addCustomFormatter(new MyDateFormatter("binder22 转换器"));
        }

        public void bar() {

        }
    }

}

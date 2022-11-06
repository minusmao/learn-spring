package com.example.learn.spring.a31;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.List;
import java.util.Map;

@Configuration
public class WebConfig {
    @ControllerAdvice
    static class MyControllerAdvice {
        /*
         * @ControllerAdvice四个功能：
         *     1、@InitBinder 添加自定义参数解析
         *     2、@ModelAttribute 添加全局model
         *     3、修饰的类实现接口ResponseBodyAdvice 扩展增强响应报文体
         *     4、@ExceptionHandler 控制器异常统一处理
         */

        @ExceptionHandler
        @ResponseBody
        public Map<String, Object> handle(Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @Bean
    public ExceptionHandlerExceptionResolver resolver() {
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
        return resolver;
    }
}

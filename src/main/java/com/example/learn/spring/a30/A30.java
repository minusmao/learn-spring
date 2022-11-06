package com.example.learn.spring.a30;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * DispatcherServlet处理异常：
 *     doService() -> doDispatch() -> processDispatchResult() -> processHandlerException()
 *     processHandlerException()方法会遍历异常处理器，进行异常处理
 *     异常处理器包含多个，拿ExceptionHandlerExceptionResolver举例（功能：把@ExceptionHandler修饰的方法，作为异常处理方法）
 */
public class A30 {
    public static void main(String[] args) throws NoSuchMethodException {
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));  // 消息转换器
        resolver.afterPropertiesSet();  // 添加默认的参数解析器、返回值处理器

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // 1.测试 json
//        HandlerMethod handlerMethod = new HandlerMethod(new Controller1(), Controller1.class.getMethod("foo"));
//        Exception e = new ArithmeticException("被零除");
//        resolver.resolveException(request, response, handlerMethod, e);  // 会查看Controller1中是否有@ExceptionHandler注解的方法
//        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
        // 2.测试 mav
//        HandlerMethod handlerMethod = new HandlerMethod(new Controller2(), Controller2.class.getMethod("foo"));
//        Exception e = new ArithmeticException("被零除");
//        ModelAndView mav = resolver.resolveException(request, response, handlerMethod, e);
//        System.out.println(mav.getModel());
//        System.out.println(mav.getViewName());
        // 3.测试嵌套异常
        //  ExceptionHandlerExceptionResolver的doResolveHandlerMethodException()会将所有异常解析出来，在422行：exToExpose.getCause()
//        HandlerMethod handlerMethod = new HandlerMethod(new Controller3(), Controller3.class.getMethod("foo"));
//        Exception e = new Exception("e1", new RuntimeException("e2", new IOException("e3")));
//        resolver.resolveException(request, response, handlerMethod, e);
//        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));

        // 4.测试异常处理方法参数解析
        HandlerMethod handlerMethod = new HandlerMethod(new Controller4(), Controller4.class.getMethod("foo"));
        Exception e = new Exception("e1");
        resolver.resolveException(request, response, handlerMethod, e);
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
        /*
            学到了什么
                a. ExceptionHandlerExceptionResolver 能够重用参数解析器、返回值处理器，实现组件重用
                b. 能够支持嵌套异常
         */
    }

    static class Controller1 {
        public void foo() {

        }

        @ExceptionHandler
        @ResponseBody
        public Map<String, Object> handle(ArithmeticException e) {
            return Map.of("error", e.getMessage());
        }
    }

    static class Controller2 {
        public void foo() {

        }

        @ExceptionHandler
        public ModelAndView handle(ArithmeticException e) {
            return new ModelAndView("test2", Map.of("error", e.getMessage()));
        }
    }

    static class Controller3 {
        public void foo() {

        }

        @ExceptionHandler
        @ResponseBody
        public Map<String, Object> handle(IOException e3) {
            return Map.of("error", e3.getMessage());
        }
    }

    static class Controller4 {
        public void foo() {
        }

        @ExceptionHandler
        @ResponseBody
        public Map<String, Object> handler(Exception e, HttpServletRequest request) {
            System.out.println(request);
            return Map.of("error", e.getMessage());
        }
    }
}

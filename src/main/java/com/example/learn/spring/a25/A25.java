package com.example.learn.spring.a25;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.*;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.*;
import com.example.learn.spring.a25.WebConfig.Controller1;
import com.example.learn.spring.a25.WebConfig.User;

import java.util.List;

/**
 * 控制器方法的执行流程
 */
public class A25 {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(WebConfig.class);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("name", "张三");

        /*
           现在可以通过ServletInvocableHandlerMethod把“拓展的四个内容”整合在一起，并完成控制器方法的调用，如下
         */
        ServletInvocableHandlerMethod handlerMethod =
                new ServletInvocableHandlerMethod(context.getBean(Controller1.class), Controller1.class.getMethod("foo", User.class));
        ServletRequestDataBinderFactory binderFactory = new ServletRequestDataBinderFactory(null, null);
        handlerMethod.setDataBinderFactory(binderFactory);   // 对象绑定、类型转换
        handlerMethod.setParameterNameDiscoverer(new DefaultParameterNameDiscoverer());    // 参数名解析
        handlerMethod.setHandlerMethodArgumentResolvers(getArgumentResolvers(context));    // 参数解析器
        ModelAndViewContainer container = new ModelAndViewContainer();

        handlerMethod.invokeAndHandle(new ServletWebRequest(request), container, null);

        System.out.println(container.getModel());

        context.close();
    }

    public static HandlerMethodArgumentResolverComposite getArgumentResolvers(AnnotationConfigApplicationContext context) {
        HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
        composite.addResolvers(
                new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), false),
                new PathVariableMethodArgumentResolver(),
                new RequestHeaderMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ServletCookieValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ExpressionValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ServletRequestMethodArgumentResolver(),
                new ServletModelAttributeMethodProcessor(false),
                new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
                new ServletModelAttributeMethodProcessor(true),
                new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), true)
        );
        return composite;
    }

    // 相关笔记
    /*
       一、HandlerMethod
         接口HandlerMethod包含：bean（控制器对象）、method（控制器方法）
         其重要子类ServletInvocableHandlerMethod拓展了一下四个内容：
            WebDataBinderFactory                      -> 负责对象绑定、类型转换
            ParameterNameDiscoverer                   -> 负责参数名解析
            HandlerMethodArgumentResolverComposite    -> 负责解析参数
            HandlerMethodReturnValueHandlerComposite  -> 负责处理返回值
       二、控制器方法执行流程1-准备
         RequestMappingHandlerAdapter --(扩展：解析@ControllerAdvice修饰类的@InitBinder方法，拿到自定义的数据转换器)--> 创建WebDataBinderFactory
         RequestMappingHandlerAdapter --(扩展：解析@ControllerAdvice修饰类的@ModelAttribute方法，拿到扩展的model数据)--> 创建ModelFactory
         ModelFactory ----> 添加解析的model数据到ModelAndViewContainer中
       三、控制器方法执行流程2-执行
         RequestMappingHandlerAdapter  ----> 调用ServletInvocableHandlerMethod的invokeAndHandle(webRequest, mavContainer)
         ServletInvocableHandlerMethod ----> 通过参数解析器解析参数（HandlerMethodArgumentResolverComposite），获取参数args
                                             有的参数解析器涉及RequestBodyAdvice（请求体增强）
                                             有的参数解析器涉及数据绑定生成模型数据，并放入ModelAndViewContainer
         ServletInvocableHandlerMethod ----> 调用method.invoke(bean, args)得到returnValue
         ServletInvocableHandlerMethod ----> 通过返回值解析器（HandlerMethodReturnValueHandlerComposite）处理returnValue
                                       ----> 有的返回值解析器涉及ResponseBodyAdvice（响应体增强）
                                       ----> 添加model数据、处理视图名、是否渲染到ModelAndViewContainer
         ServletInvocableHandlerMethod ----> 从ModelAndViewContainer中拿到最终的ModelAndView对象，并返回给RequestMappingHandlerAdapter

    */

}

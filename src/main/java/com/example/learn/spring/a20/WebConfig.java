package com.example.learn.spring.a20;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Configuration
@ComponentScan  // 默认扫描当前配置所在包下的所有类
@PropertySource("classpath:application.properties")  // 读取指定配置文件
@EnableConfigurationProperties({WebMvcProperties.class, ServerProperties.class})  // 将配置文件的信息绑定到对象上
public class WebConfig {
    // ⬅️内嵌 web 容器工厂
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory(ServerProperties serverProperties) {
        return new TomcatServletWebServerFactory(serverProperties.getPort());
    }

    // ⬅️创建 DispatcherServlet
    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();    // 其核心初始化方法onRefresh()由tomcat管理调用
    }

    /*
      DispatcherServlet的onRefresh()方法调用了initStrategies(context)方法
      该方法负责调用各个初始化：
        protected void initStrategies(ApplicationContext context) {
            initMultipartResolver(context);    // 文件上传解析器
            initLocaleResolver(context);       // 本地化解析器（根据不同地区返回语言）
            initThemeResolver(context);
            initHandlerMappings(context);      // 路径映射解析器
            initHandlerAdapters(context);      // 适配器（适配不同形式的Controller控制器方法）
            initHandlerExceptionResolvers(context);    // 解析异常
            initRequestToViewNameTranslator(context);
            initViewResolvers(context);
            initFlashMapManager(context);
        }
      初始化的基本逻辑如下：判断是否扫描父容器的所有bean然后进行扫描，，如果最终扫描不到自定义的bean，
                        则加载默认配置文件里DispatcherServlet.properties指定的类
      拿initHandlerMappings()方法举例：
        如果容器中没有注册HandlerMapper类型的bean，则加载DispatcherServlet.properties指定的HandlerMapping类，配置文件内容如下：
          org.springframework.web.servlet.HandlerMapping=org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\
	          org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping,\
	          org.springframework.web.servlet.function.support.RouterFunctionMapping
	    一共默认三个HandlerMapping类，其中RequestMappingHandlerMapping就是负责解析@RequestMapping注解
    */

    // ⬅️注册 DispatcherServlet, Spring MVC 的入口
    @Bean
    public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(
            DispatcherServlet dispatcherServlet, WebMvcProperties webMvcProperties) {
        DispatcherServletRegistrationBean registrationBean = new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        // 设置loadOnStartup值，大于0时，则会在tomcat启动后加载（调用servlet的onRefresh方法）
        registrationBean.setLoadOnStartup(webMvcProperties.getServlet().getLoadOnStartup());
        return registrationBean;
    }

    // 如果用 DispatcherServlet 初始化时默认添加的组件, 并不会作为 bean, 给测试带来困扰
    // ⬅️1. 加入RequestMappingHandlerMapping
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    // ⬅️2. 继续加入RequestMappingHandlerAdapter, 会替换掉 DispatcherServlet 默认的 4 个 HandlerAdapter
    @Bean
    public MyRequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        TokenArgumentResolver tokenArgumentResolver = new TokenArgumentResolver();
        YmlReturnValueHandler ymlReturnValueHandler = new YmlReturnValueHandler();
        MyRequestMappingHandlerAdapter handlerAdapter = new MyRequestMappingHandlerAdapter();
        // 设置参数处理器
        /*
           源码中HandlerAdapter继承了InitializingBean，会在afterPropertiesSet()中添加所有的已有处理器，
           以及此处的设置customArgumentResolvers到argumentResolvers中，所以这里添加的自定义处理器列表，不会去掉原先已有的处理器
         */
        handlerAdapter.setCustomArgumentResolvers(List.of(tokenArgumentResolver));
        // 设置返回值处理器
        handlerAdapter.setCustomReturnValueHandlers(List.of(ymlReturnValueHandler));
        return handlerAdapter;
    }

    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters();
    }

    // ⬅️3. 演示 RequestMappingHandlerAdapter 初始化后, 有哪些参数、返回值处理器

    // ⬅️3.1 创建自定义参数处理器

    // ⬅️3.2 创建自定义返回值处理器

}

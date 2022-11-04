package com.example.learn.spring.a28;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class A28 {
    public static void main(String[] args) throws IOException, NoSuchMethodException, HttpMediaTypeNotAcceptableException {
//        test1();
//        test2();
        test3();
//        test4();

        /*
            学到了什么
                a. MessageConverter 的作用, @ResponseBody 是返回值处理器解析的, 但具体转换工作是 MessageConverter 做的
                b. 如何选择 MediaType
                    - 首先看 @RequestMapping 上有没有指定
                    - 其次看 request 的 Accept 头有没有指定
                    - 最后按 MessageConverter 的顺序, 谁能谁先转换
         */

    }

    /**
     * 返回值处理器中有多个Converter时，优先级问题
     * 最终由优先级最高的Converter做类型转换（对象转json、对象转xml等）
     */
    private static void test4() throws IOException, HttpMediaTypeNotAcceptableException, NoSuchMethodException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletWebRequest webRequest = new ServletWebRequest(request, response);

        request.addHeader("Accept", "application/xml");  // 优先级次之
        response.setContentType("application/json");      // 优先级最高（可以通过@RequestMapping(produces = "application/json")设置）

        // Converter在list中的顺序，优先级最低
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(
                List.of(
                        new MappingJackson2HttpMessageConverter(), new MappingJackson2XmlHttpMessageConverter()
                ));
        processor.handleReturnValue(
                new User("张三", 18),
                new MethodParameter(A28.class.getMethod("user"), -1),
                new ModelAndViewContainer(),
                webRequest
        );
        System.out.println(new String(response.getContentAsByteArray(), StandardCharsets.UTF_8));
    }

    @ResponseBody
    @RequestMapping(produces = "application/json")  // produces属性设置了response.setContentType("application/json")
    public User user() {
        return null;
    }

    /**
     * json转对象
     */
    private static void test3() throws IOException {
        MockHttpInputMessage message = new MockHttpInputMessage("""
                {
                    "name":"李四",
                    "age":20
                }
                """.getBytes(StandardCharsets.UTF_8));
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        if (converter.canRead(User.class, MediaType.APPLICATION_JSON)) {
            Object read = converter.read(User.class, message);
            System.out.println(read);
        }
    }

    /**
     * 对象转xml
     */
    private static void test2() throws IOException {
        // 消息对象
        MockHttpOutputMessage message = new MockHttpOutputMessage();
        // 基于Jackson的对象转xml
        MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();
        // 判断是否可转换xml类型
        if (converter.canWrite(User.class, MediaType.APPLICATION_XML)) {
            // 转换
            converter.write(new User("李四", 20), MediaType.APPLICATION_XML, message);
            System.out.println(message.getBodyAsString());
        }
    }

    /**
     * 对象转json
     */
    public static void test1() throws IOException {
        // 消息对象
        MockHttpOutputMessage message = new MockHttpOutputMessage();
        // 基于Jackson的对象转json
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 判断是否可转换JSON类型
        if (converter.canWrite(User.class, MediaType.APPLICATION_JSON)) {
            // 转换
            converter.write(new User("张三", 18), MediaType.APPLICATION_JSON, message);
            System.out.println(message.getBodyAsString());
        }
    }

    public static class User {
        private String name;
        private int age;

        @JsonCreator
        public User(@JsonProperty("name") String name, @JsonProperty("age") int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "User{" +
                   "name='" + name + '\'' +
                   ", age=" + age +
                   '}';
        }
    }
}

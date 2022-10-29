package org.springframework.aop.framework.autoproxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;

/**
 * 模拟：切面后处理器
 */
public class A17 {

    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("aspect1", Aspect1.class);
        context.registerBean("config", Config.class);
        context.registerBean(ConfigurationClassPostProcessor.class);    // @Configuration注解后处理器
        context.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);    // 切面后处理器
        // 该切面后处理器是BeanPostProcessor，会在Bean生命周期的以下(*)处做处理
        // 创建 -> (*) 依赖注入 -> 初始化 (*)
        // 包含两个重要方法：
        //   findEligibleAdvisors() - 寻找有资格的切面类，在查找过程中，如果是advisor就直接加入列表，如果是Aspect就会被转换成advisor并加入列表
        //   wrapIfNecessary()      - 判断目标是否有必要创建代理（匹配定义的切点），如果匹配，则为其创建代理类，进而实现AOP的效果

        context.refresh();
        System.out.println(">>> 打印容器中的bean名：");
        for (String name : context.getBeanDefinitionNames()) {
            System.out.println(name);
        }

        /* 手动调用，观察内部实现逻辑 */
        /*
            第一个重要方法 findEligibleAdvisors 找到有【资格】的 Advisors
                a. 有【资格】的 Advisor 一部分是低级的, 可以由自己编写, 如下例中的 advisor3
                b. 有【资格】的 Advisor 另一部分是高级的, 由本章的主角解析 @Aspect 后获得
         */
        AnnotationAwareAspectJAutoProxyCreator creator = context.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        List<Advisor> advisors = creator.findEligibleAdvisors(Target1.class, "target1");// 寻找匹配Target1的所有切面
        System.out.println(">>> 打印匹配Target1的所有切面：");
        for (Advisor advisor : advisors) {
            System.out.println(advisor);
        }
        // 找到4个切面，其中第一个**.ExposeInvocationInterceptor.ADVISOR是spring会给所有的都会加的切面，后三个是我们定义的切面

        /*
            第二个重要方法 wrapIfNecessary
                a. 它内部调用 findEligibleAdvisors, 只要返回集合不空, 则表示需要创建代理
         */
        System.out.println(">>> 打印是否是代理类：");
        Object o1 = creator.wrapIfNecessary(new Target1(), "target1", "target1");
        System.out.println("o1 ->" + o1.getClass());  // 是代理类：A17$Target1$$EnhancerBySpringCGLIB$$d0f381dc
        Object o2 = creator.wrapIfNecessary(new Target2(), "target2", "target2");
        System.out.println("o2 ->" + o2.getClass());  // 是本身：A17$Target2

        // 调用方法
        ((Target1) o1).foo();

        /*
            学到了什么
                a. 自动代理后处理器 AnnotationAwareAspectJAutoProxyCreator 会帮我们创建代理
                b. 通常代理创建的活在原始对象初始化后执行, 但碰到循环依赖会提前至依赖注入之前执行
                c. 高级的 @Aspect 切面会转换为低级的 Advisor 切面, 理解原理, 大道至简
         */
    }

    // 目标对象1
    static class Target1 {
        public void foo() {
            System.out.println("target1 foo");
        }
    }

    // 目标对象2
    static class Target2 {
        public void var() {
            System.out.println("target2 bar");
        }
    }

    // 切面：@Aspect方式
    @Aspect    // 高级切面类
    static class Aspect1 {
        @Before("execution(* foo())")
        public void before() {
            System.out.println("aspect1 before...");
        }

        @After("execution(* foo())")
        public void after() {
            System.out.println("aspect1 after...");
        }
    }

    // 切面：Advisor方式
    @Configuration
    static class Config {
        @Bean  // 低级切面
        public Advisor advisor3(MethodInterceptor advice3) {
            // 切点定义
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* foo())");
            // 返回切面：需要切点和通知
            return new DefaultPointcutAdvisor(pointcut, advice3);
        }

        @Bean  // 通知
        public MethodInterceptor advice3() {
            return new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    System.out.println("advice3 before...");
                    Object result = invocation.proceed();// 调用真正的业务方法
                    System.out.println("advice3 after...");
                    return result;
                }
            };
        }
    }

}

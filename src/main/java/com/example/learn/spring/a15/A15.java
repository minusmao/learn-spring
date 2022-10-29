package com.example.learn.spring.a15;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class A15 {
    public static void main(String[] args) {
        /*
            两个切面概念
            aspect =
                通知1(advice) +  切点1(pointcut)
                通知2(advice) +  切点2(pointcut)
                通知3(advice) +  切点3(pointcut)
                ...
            advisor = 更细粒度的切面，包含一个通知和切点

            补充：在底层实现上，aspect最终会被拆解成多个advisor
         */

        // 1. 备好切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* foo())");    // 设置切点表达式
        // 2. 备好通知
        // 补充：最基本的通知MethodInterceptor（环绕通知），其他通知都会被转换成此通知类型执行
        MethodInterceptor advice = invocation -> {
            System.out.println("before...");
            Object result = invocation.proceed(); // 调用目标
            System.out.println("after...");
            return result;
        };
        // 3. 备好切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);

        /*
           4. 创建代理
                a. proxyTargetClass = false, 目标实现了接口, 用 jdk 实现
                b. proxyTargetClass = false,  目标没有实现接口, 用 cglib 实现
                c. proxyTargetClass = true, 总是使用 cglib 实现
         */
        Target1 target = new Target1();
        ProxyFactory factory = new ProxyFactory();    // ProxyFactory会根据情况选择jdk、cglib来实现
        factory.setTarget(target);
        factory.addAdvisor(advisor);                  // addAdvisors()绑定多个切点
//        factory.setInterfaces(target.getClass().getInterfaces());   // 需要告诉工厂实现的接口类型，工厂才会知道目标对象实现了接口
        factory.setProxyTargetClass(false);           // 设置proxyTargetClass控制代理实现方式
        I1 proxy = (I1) factory.getProxy();
        System.out.println(proxy.getClass());   // 查看代理类类型打印$Proxy0为jdk实现，打印$$EnhancerBySpringCGLIB$$为cglib实现
        proxy.foo();
        proxy.bar();
        /*
            学到了什么
                a. Spring 的代理选择规则
                b. 底层的切点实现
                c. 底层的通知实现
                d. ProxyFactory 是用来创建代理的核心实现, 用 AopProxyFactory 选择具体代理实现
                    - JdkDynamicAopProxy
                    - ObjenesisCglibAopProxy
         */
    }

    interface I1 {
        void foo();

        void bar();
    }

    static class Target1 implements I1 {
        public void foo() {
            System.out.println("target1 foo");
        }

        public void bar() {
            System.out.println("target1 bar");
        }
    }

    static class Target2 {
        public void foo() {
            System.out.println("target2 foo");
        }

        public void bar() {
            System.out.println("target2 bar");
        }
    }
}

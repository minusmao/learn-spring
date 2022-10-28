package com.example.learn.spring.a12;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * note: jdk代理Proxy.newProxyInstance()内部是同asm动态生成代理类的，看源码比较困难，下面是手写模拟实现
 */
public class A12 {

    interface Foo {
        void foo();
        int bar();
    }

    static class Target implements Foo {
        public void foo() {
            System.out.println("target foo");
        }

        @Override
        public int bar() {
            System.out.println("target bar");
            return 100;
        }
    }

    // 自定义的InvocationHandler，模仿jdk实现
    /*
    interface MyInvocationHandler {
        Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
    }
    */

    public static void main(String[] param) {
        Foo proxy = new $Proxy0(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
                // 1. 功能增强
                System.out.println("before...");
                // 2. 调用目标
//                new Target().foo();
                return method.invoke(new Target(), args);
            }
        });
        proxy.foo();
        proxy.bar();
        /*
            学到了什么: 代理一点都不难, 无非就是利用了多态、反射的知识
                1. 方法重写可以增强逻辑, 只不过这【增强逻辑】千变万化, 不能写死在代理内部
                2. 通过接口回调将【增强逻辑】置于代理类之外
                3. 配合接口方法反射(也是多态), 就可以再联动调用目标方法
         */
    }
}

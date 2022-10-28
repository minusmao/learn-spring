package com.example.learn.spring.a12;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射的自我优化
 */
// 运行时请添加 --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/jdk.internal.reflect=ALL-UNNAMED
public class TestMethodInvoke {
    public static void main(String[] args) throws Exception {
        Method foo = TestMethodInvoke.class.getMethod("foo", int.class);
        for (int i = 1; i <= 17; i++) {
            show(i, foo);
            // 当Method第一次调用时，将创建一个NativeMethodAccessorImpl来实现反射调用方法，性能较低
            // 当第17次时，将创建一个GeneratedMethodAccessor类来实现，将不再进行反射调用，而是直接通过它调用，性能较高
            // 代价：每个方法都会生成一个代理类来实现。（而cglib时将一个类实现两个代理类，代理所有方法）
            foo.invoke(null, i);
        }
        System.in.read();
    }

    // 方法反射调用时, 底层 MethodAccessor 的实现类
    private static void show(int i, Method foo) throws Exception {
        Method getMethodAccessor = Method.class.getDeclaredMethod("getMethodAccessor");
        getMethodAccessor.setAccessible(true);
        Object invoke = getMethodAccessor.invoke(foo);
        if (invoke == null) {
            System.out.println(i + ":" + null);
            return;
        }
        Field delegate = Class.forName("jdk.internal.reflect.DelegatingMethodAccessorImpl").getDeclaredField("delegate");
        delegate.setAccessible(true);
        System.out.println(i + ":" + delegate.get(invoke));
    }

    public static void foo(int i) {
        System.out.println(i + ":" + "foo");
    }
}

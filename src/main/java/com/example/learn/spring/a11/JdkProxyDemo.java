package com.example.learn.spring.a11;
import java.io.IOException;
import java.lang.reflect.Proxy;

public class JdkProxyDemo {

    interface Foo {
        void foo();
    }

    static final class Target implements Foo {
        public void foo() {
            System.out.println("target foo");
        }
    }

    /*
      jdk代理的特点：1、代理对象和目标对象不能互相强制类型转换，只能转换成他们共同实现的接口
                   2、目标对象可以为final类型
     */

    // jdk 只能针对接口代理
    // cglib
    public static void main(String[] param) throws IOException {
        // 目标对象
        Target target = new Target();

        // note: 代理类是运行期间动态生成的，所以需要用到ClassLoader把动态生成的字节码加载成对象
        ClassLoader loader = JdkProxyDemo.class.getClassLoader(); // 用来加载在运行期间动态生成的字节码
        Foo proxy = (Foo) Proxy.newProxyInstance(loader, new Class[]{Foo.class}, (p, method, args) -> {
            System.out.println("before...");
            // 目标.方法(参数)
            // 方法.invoke(目标, 参数);
            Object result = method.invoke(target, args);
            System.out.println("after....");
            return result; // 让代理也返回目标方法执行的结果
        });

        System.out.println(proxy.getClass());

        proxy.foo();

        // 补充：用arthas工具查看源码发现，jdk代理还默认代理了hashCode()、equals()、toString()这三个方法
        proxy.toString();

        System.in.read();
    }
}

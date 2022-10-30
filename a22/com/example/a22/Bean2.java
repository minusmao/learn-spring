package com.example.a22;

public class Bean2 {
    public void foo(String name, int age) {
        System.out.println(name + ": " + age);
    }

    /*
        常规javac编译后，用idea打开.class文件发现，参数名丢失，形式如下：
            public void foo(String var1, int var2) {
                System.out.println(var1 + ": " + var2);
            }
        1、使用javac -parameters编译后，会保留参数名
        用javap -c -v Bean2.class反编译，发现末尾多了参数名的信息，形式如下：
            MethodParameters:
              Name                           Flags
              name
              age
        2、使用javac -g编译后，也会保留参数名，保存位置名为本地变量表，但是不能通过反射api获取，需通过asm方式获取
        用javap -c -v Bean2.class反编译，发现末尾多了参数名的信息，形式如下：
            LocalVariableTable:
                Start  Length  Slot  Name   Signature
                    0      30     0  this   Lcom/example/a22/Bean2;
                    0      30     1  name   Ljava/lang/String;
                    0      30     2   age   I
        补充: 本地变量表对接口无效
    */
}

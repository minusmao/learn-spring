package com.example.learn.spring.a23;

/**
 * 对象绑定与类型转换
 *   两套底层接口
 *   一套高层接口
 */
public class A23 {
    /*
       1、底层第一套接口（spring提供）
             Printer   -> 把其它类型转为String
             Parser    -> 把String转为其它类型
             Formatter -> 综合Printer与Parser功能
             Converter -> 把类型S转为类型T
          Printer、Parser、Converter经过适配转换成GenericConverter放入Converters集合
          ConversionService接口的FormattingConversionService子类，利用它们实现类型转换

       2、底层第二套接口（jdk提供）
             PropertyEditor -> 把String与其它类型相互转换
          PropertyEditorRegistry可以注册多个PropertyEditor对象
          与第一套接口直接可以通过FormatterPropertyEditorAdapter来进行适配

       3、高层接口TypeConverter
         Facade门面模式：TypeConverter包含一个TypeConverterDelegate委派类，在实现转换时，
                       会通过TypeConverterDelegate委派ConversionService或PropertyEditorRegistry执行真正的转换工作
         TypeConverterDelegate委派类工作流程：
             首先看是否有自定义的转换器（PropertyEditor），@InitBinder添加的即属于这种（用了适配器模式把Formatter转为需要的PropertyEditor）
                 * 补充@InitBinder的使用：https://blog.csdn.net/wang0907/article/details/108357696
             失败，则再看有没有ConversionService转换
             失败，则再利用默认的PropertyEditor转换
             最后有一些特殊处理
         TypeConverter接口的四个实现：
             SimpleTypeConverter -> 仅做类型转换
             BeanWrapperImpl     -> 为bean的属性赋值，当需要时做类型转换，走Property（通过set方法）
             DirectFieldAccessor -> 为bean的属性赋值，当需要时做类型转换，走Field（直接访问成员变量）
             ServletRequestDataBinder -> 为bean的属性赋值，当需要时做类型转换，根据directFieldAccess选择走Property还是Field，具备校验与获取校验结果功能
    */
}

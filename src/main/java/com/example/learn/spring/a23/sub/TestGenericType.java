package com.example.learn.spring.a23.sub;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 获取泛型参数
 */
public class TestGenericType {
    public static void main(String[] args) {
        // 小技巧
        // 1. java api
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
        Type type = TeacherDao.class.getGenericSuperclass();
        System.out.println(type);

        if (type instanceof ParameterizedType parameterizedType) {
            System.out.println(parameterizedType.getActualTypeArguments()[0]);
        }

        // 2. spring api 1
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
        Class<?> t = GenericTypeResolver.resolveTypeArgument(TeacherDao.class, BaseDao.class);// 获取多个泛型是resolveTypeArguments()
        System.out.println(t);

        // 3. spring api 2
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(ResolvableType.forClass(TeacherDao.class).getSuperType().getGeneric().resolve());
    }

}

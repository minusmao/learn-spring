package com.example.learn.spring.a41;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

public class TestDataSourceAuto {
    @SuppressWarnings("all")
    public static void main(String[] args) {
        GenericApplicationContext context = new GenericApplicationContext();

        // 增加配置信息
        StandardEnvironment env = new StandardEnvironment();
        env.getPropertySources().addLast(new SimpleCommandLinePropertySource(
                "--spring.datasource.url=jdbc:mysql://localhost:3306/test",
                "--spring.datasource.username=root",
                "--spring.datasource.password=root"
        ));
        context.setEnvironment(env);

        AnnotationConfigUtils.registerAnnotationConfigProcessors(context.getDefaultListableBeanFactory());
        context.registerBean(Config.class);

        // 配置扫描包名   这里的作用跟@SpringBootApplication注解中的@EnableAutoConfiguration中的@AutoConfigurationPackage功能类似
        // 默认mapper扫描也会取这里配置的值
        String packageName = TestDataSourceAuto.class.getPackageName();
        System.out.println("当前包名:" + packageName);
        AutoConfigurationPackages.register(context.getDefaultListableBeanFactory(),
                packageName);

        context.refresh();
        for (String name : context.getBeanDefinitionNames()) {
            String resourceDescription = context.getBeanDefinition(name).getResourceDescription();
            if (resourceDescription != null)
                System.out.println(name + " 来源:" + resourceDescription);
        }

        DataSourceProperties dataSourceProperties = context.getBean(DataSourceProperties.class);
        System.out.println(dataSourceProperties.getUrl());
        System.out.println(dataSourceProperties.getPassword());
        System.out.println(dataSourceProperties.getUsername());
    }

    @Configuration
    @Import(MyImportSelector.class)
    static class Config {

    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    DataSourceAutoConfiguration.class.getName(),  // 数据源自动配置类
                    MybatisAutoConfiguration.class.getName(),  // mybatis自动配置类：SqlSession工厂、Mapper扫描
                    DataSourceTransactionManagerAutoConfiguration.class.getName(),  // 基于DataSource的事务管理器
                    TransactionAutoConfiguration.class.getName()  // 声明式的事务管理：提供了事务切面、切点、控制
            };

            /*
                MybatisAutoConfiguration中的：
                  @ConditionalOnSingleCandidate(DataSource.class) 表示有且只有一个DataSource
                  @AutoConfigureAfter({ DataSourceAutoConfiguration.class, MybatisLanguageDriverAutoConfiguration.class })
                    控制顺序，必须在DataSourceAutoConfiguration、MybatisLanguageDriverAutoConfiguration之后
            */
        }
    }
}

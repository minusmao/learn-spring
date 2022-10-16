package com.example.learn.spring.a05;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.learn.spring.a05.mapper.Mapper1;
import com.example.learn.spring.a05.mapper.Mapper2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.example.learn.spring.a05.component")
public class Config {
    @Bean
    public Bean1 bean1() {
        return new Bean1();
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }

    @Bean(initMethod = "init")
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    // bean实例化的三种方法：1、调用构造器构造bean实例；2、BeanFactory调用某个类的静态工厂方法创建bean；3、BeanFactory调用实例工厂方法创建bean

    // spring并不能管理mapper接口，实际上管理的是MapperFactoryBean<Mapper泛型>工厂对象
    // 在spring的实现中，每一Mapper，都会对应一个MapperFactoryBean
    // 此外，mapper需要sqlSessionFactoryBean
//    @Bean
//    public MapperFactoryBean<Mapper1> mapper1(SqlSessionFactory sqlSessionFactory) {
//        MapperFactoryBean<Mapper1> factory = new MapperFactoryBean<>(Mapper1.class);
//        factory.setSqlSessionFactory(sqlSessionFactory);
//        return factory;
//    }
//
//    @Bean
//    public MapperFactoryBean<Mapper2> mapper2(SqlSessionFactory sqlSessionFactory) {
//        MapperFactoryBean<Mapper2> factory = new MapperFactoryBean<>(Mapper2.class);
//        factory.setSqlSessionFactory(sqlSessionFactory);
//        return factory;
//    }

}

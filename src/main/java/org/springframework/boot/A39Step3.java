package org.springframework.boot;

import org.springframework.boot.ApplicationEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;

/**
 * spring-boot的run()方法步骤3：准备Environment添加命令行参数（*）
 */
public class A39Step3 {
    public static void main(String[] args) throws IOException {
        // systemProperties系统属性（Java虚拟机参数） systemEnvironment系统环境变量
        ApplicationEnvironment env = new ApplicationEnvironment(); // 系统环境变量, properties, yaml
        // 自定义添加配置文件来源（此行代码只是功能示例，源码中添加配置文件来源没在步骤3中，而是在步骤5）
        env.getPropertySources().addLast(new ResourcePropertySource(new ClassPathResource("step3.properties")));
        env.getPropertySources().addFirst(new SimpleCommandLinePropertySource(args));// 自定义添加命令行参数来源  commandLineArgs
        for (PropertySource<?> ps : env.getPropertySources()) {
            System.out.println(ps);
        }
        System.out.println(env.getProperty("JAVA_HOME"));

        System.out.println(env.getProperty("server.port"));
    }
}

package org.springframework.boot;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Map;

/**
 * spring-boot的run()方法步骤7：打印 banner（*）
 */
public class A39Step7 {
    public static void main(String[] args) {
        ApplicationEnvironment env = new ApplicationEnvironment();
        SpringApplicationBannerPrinter printer = new SpringApplicationBannerPrinter(
                new DefaultResourceLoader(),
                new SpringBootBanner()  // 默认banner
        );
        // 测试文字 banner（这里使用了Java硬编码的方式添加了配置信息，也可以直接在配置文件中写配置信息spring.banner.location=banner1.txt）
//        env.getPropertySources().addLast(new MapPropertySource("custom", Map.of("spring.banner.location","banner1.txt")));
        // 测试图片 banner
        env.getPropertySources().addLast(new MapPropertySource("custom", Map.of("spring.banner.image.location","banner2.png")));
        // 版本号的获取
        System.out.println(SpringBootVersion.getVersion());  // 原理：获取spring-boot的jar包中的MANIFEST.MF文件中的版本信息
        printer.print(env, A39Step7.class, System.out);
    }
}

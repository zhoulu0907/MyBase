package com.cmsr.onebase.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 项目的启动类
 * sj
 */
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.cmsr.onebase", "org.anyline"})
public class OneBaseServerApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(OneBaseServerApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("应用启动失败，错误信息: " + e.getMessage());
            throw e;
        }
    }

}

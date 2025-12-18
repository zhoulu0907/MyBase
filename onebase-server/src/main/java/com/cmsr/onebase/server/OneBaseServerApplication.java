package com.cmsr.onebase.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 项目的启动类
 * sj
 */
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.cmsr", "org.anyline"})
public class OneBaseServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneBaseServerApplication.class, args);
    }

}

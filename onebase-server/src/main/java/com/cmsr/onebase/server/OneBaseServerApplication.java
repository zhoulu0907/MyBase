package com.cmsr.onebase.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 */
@SpringBootApplication(scanBasePackages = {"com.cmsr.onebase", "org.anyline"})
public class OneBaseServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneBaseServerApplication.class, args);
    }

}

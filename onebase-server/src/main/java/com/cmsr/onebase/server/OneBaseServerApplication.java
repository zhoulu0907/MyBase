package com.cmsr.onebase.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 项目的启动类
 */
@SpringBootApplication(scanBasePackages = {"com.cmsr.onebase", "org.anyline"})
@EnableTransactionManagement
public class OneBaseServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneBaseServerApplication.class, args);
    }

}

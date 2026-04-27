package com.cmsr.onebase.server.flink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 */
@SpringBootApplication(scanBasePackages = {"com.cmsr.onebase" })
public class OneBaseServerFlinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneBaseServerFlinkApplication.class, args);
    }

}

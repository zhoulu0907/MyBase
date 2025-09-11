package com.cmsr.onebase.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${onebase.info.base-package}
@SpringBootApplication(scanBasePackages = "com.cmsr.onebase")
public class OneBaseServerApplication {

    public static void main(String[] args) {


        SpringApplication.run(OneBaseServerApplication.class, args);

    }

}

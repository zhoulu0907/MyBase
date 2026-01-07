package com.cmsr.onebase.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 项目的启动类
 * sj
 */
@EnableAsync
@EnableScheduling
@MapperScan({"com.cmsr.onebase.**.mapper"})
@SpringBootApplication(scanBasePackages = {"com.cmsr.onebase", "org.anyline"})
public class OneBaseServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneBaseServerApplication.class, args);
    }

}

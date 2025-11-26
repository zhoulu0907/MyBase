package com.cmsr.onebase.server.runtime;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 */
@MapperScan({"com.cmsr.onebase.**.mapper"})
@SpringBootApplication(scanBasePackages = {"com.cmsr.onebase", "org.anyline"})
public class OneBaseServerRuntimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(OneBaseServerRuntimeApplication.class, args);
    }

}

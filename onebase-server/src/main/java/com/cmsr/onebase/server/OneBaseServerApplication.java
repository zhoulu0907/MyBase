package com.cmsr.onebase.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 *
 */
@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${yudao.info.base-package}
@SpringBootApplication(scanBasePackages = {"${yudao.info.base-package}.server", "${yudao.info.base-package}.module"},
        excludeName = {
        // RPC 相关
        // "org.springframework.cloud.openfeign.FeignAutoConfiguration",
        // "com.cmsr.onebase.module.system.framework.rpc.config.RpcConfiguration"
        })
public class OneBaseServerApplication {

    public static void main(String[] args) {


        SpringApplication.run(OneBaseServerApplication.class, args);     
        
    }

}

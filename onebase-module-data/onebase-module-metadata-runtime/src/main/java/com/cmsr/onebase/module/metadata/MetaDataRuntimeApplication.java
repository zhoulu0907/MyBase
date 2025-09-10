package com.cmsr.onebase.module.metadata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 元数据模块 Runtime 启动类（如需要独立运行时使用）
 */
@SpringBootApplication(scanBasePackages = "com.cmsr.onebase")
public class MetaDataRuntimeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MetaDataRuntimeApplication.class, args);
    }
}

package com.cmsr.onebase.plugin.ocr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * OCR 插件独立启动类 (用于开发调试)
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.cmsr.onebase.plugin.ocr",
    "com.cmsr.onebase.plugin.ocr.mock" // 扫描 Mock 服务
})
public class OcrPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcrPluginApplication.class, args);
    }
}

package com.cmsr.onebase.plugin.runtime.test;

import com.cmsr.onebase.plugin.runtime.config.PluginRuntimeAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * 测试应用配置类
 * <p>
 * 为 Spring Boot 集成测试提供应用上下文配置
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
@SpringBootApplication
@Import(PluginRuntimeAutoConfiguration.class)
public class TestConfig {
    // 测试配置类，Spring Boot 测试会自动扫描并使用
}

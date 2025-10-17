package com.cmsr.onebase.framework.dolphins.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * DolphinScheduler 配置属性类
 *
 * @author matianyu
 * @date 2025-01-17
 */
@Data
@Validated
@ConfigurationProperties(prefix = "onebase.dolphinscheduler")
public class DolphinSchedulerProperties {

    /**
     * DolphinScheduler 服务基础 URL
     */
    @NotBlank(message = "baseUrl 不能为空")
    private String baseUrl;

    /**
     * 访问认证 token
     */
    @NotBlank(message = "token 不能为空")
    private String token;

    /**
     * 连接超时时间，默认 5 秒
     */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /**
     * 读取超时时间，默认 30 秒
     */
    private Duration readTimeout = Duration.ofSeconds(30);

    /**
     * 写入超时时间，默认 30 秒
     */
    private Duration writeTimeout = Duration.ofSeconds(30);

    /**
     * 日志级别：NONE/BASIC/HEADERS/BODY
     */
    private LogLevel logLevel = LogLevel.BASIC;

    /**
     * 是否启用重试，默认 true
     */
    private boolean retryEnabled = true;

    /**
     * 最大重试次数，默认 3 次
     */
    private int maxRetries = 3;

    /**
     * 日志级别枚举
     */
    public enum LogLevel {
        /** 不记录日志 */
        NONE,
        /** 记录请求和响应的基本信息 */
        BASIC,
        /** 记录请求和响应的头信息 */
        HEADERS,
        /** 记录请求和响应的完整内容 */
        BODY
    }
}

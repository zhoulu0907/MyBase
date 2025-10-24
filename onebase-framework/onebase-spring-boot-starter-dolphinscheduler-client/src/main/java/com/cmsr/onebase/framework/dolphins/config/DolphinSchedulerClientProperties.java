package com.cmsr.onebase.framework.dolphins.config;

import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * DolphinScheduler Client 配置属性类
 *
 * @author matianyu
 * @date 2025-10-23
 */
@Data
@Validated
@ConfigurationProperties(prefix = "onebase.dolphinscheduler")
public class DolphinSchedulerClientProperties {

    /** DolphinScheduler 基础地址，必须以 / 结尾，例如：http://127.0.0.1:12345/dolphinscheduler/ */
    @NotBlank(message = "baseUrl 不能为空")
    private String baseUrl;

    /** 访问认证 token */
    @NotBlank(message = "token 不能为空")
    private String token;

    /** 工作流用projectCode */
    private String projectCodeFlow;

    /** ETL用projectCode */
    private String projectCodeETL;

    /** 租户code */
    private String tenantCode;

    /** 连接超时时间，默认 5s */
    private Duration connectTimeout = Duration.ofSeconds(10);

    /** 读取超时时间，默认 30s */
    private Duration readTimeout = Duration.ofSeconds(30);

    /** 写入超时时间，默认 30s */
    private Duration writeTimeout = Duration.ofSeconds(30);

    /** 日志级别：TRACE/DEBUG/INFO/WARN/ERROR，默认 INFO */
    private String logLevel = "INFO";
}

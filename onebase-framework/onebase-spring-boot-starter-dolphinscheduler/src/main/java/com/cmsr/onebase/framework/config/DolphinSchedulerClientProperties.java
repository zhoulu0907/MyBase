package com.cmsr.onebase.framework.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * DolphinScheduler 客户端配置属性
 *
 * @author matianyu
 * @date 2025-10-15
 */
@Validated
@ConfigurationProperties(prefix = "onebase.dolphinscheduler.client")
public class DolphinSchedulerClientProperties {

    /** 是否启用自动装配 */
    private boolean enabled = true;

    /**
     * DolphinScheduler 服务基础地址，必须以 / 结尾，例如：http://127.0.0.1:12345/dolphinscheduler/
     */
    @NotBlank(message = "dolphinscheduler baseUrl 不能为空")
    private String baseUrl;

    /** 认证 Token */
    @NotBlank(message = "dolphinscheduler token 不能为空")
    private String token;

    /** 连接超时 */
    @NotNull
    private Duration connectTimeout = Duration.ofSeconds(5);

    /** 读取超时 */
    @NotNull
    private Duration readTimeout = Duration.ofSeconds(30);

    /** 写入超时 */
    @NotNull
    private Duration writeTimeout = Duration.ofSeconds(30);

    /** 日志级别：NONE/BASIC/HEADERS/BODY */
    @NotBlank
    private String logLevel = "BASIC";

    /** 是否启用真实连通性测试（仅测试环境使用） */
    private boolean enableLiveConnectivityTest = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isEnableLiveConnectivityTest() {
        return enableLiveConnectivityTest;
    }

    public void setEnableLiveConnectivityTest(boolean enableLiveConnectivityTest) {
        this.enableLiveConnectivityTest = enableLiveConnectivityTest;
    }
}

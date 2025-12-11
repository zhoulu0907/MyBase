package com.cmsr.onebase.framework.signature.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * API签名配置属性类
 *
 * @author matianyu
 * @date 2025-12-11
 */
@ConfigurationProperties(prefix = "onebase.sign")
public class ApiSignatureProperties {

    /**
     * 签名请求超时时间，单位：秒
     * 默认值：180秒
     */
    private Integer requestTimeout = 180;

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}

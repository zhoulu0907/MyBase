package com.cmsr.onebase.module.formula.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 公式引擎配置属性
 *
 * @author matianyu
 * @date 2025-09-01
 */
@Data
@Component
@ConfigurationProperties(prefix = "onebase.formula")
public class FormulaEngineProperties {

    /**
     * 是否启用公式引擎
     */
    private boolean enabled = true;

    /**
     * JavaScript引擎超时时间（毫秒）
     */
    private long timeoutMs = 5000L;

    /**
     * 是否启用安全模式（限制JavaScript功能）
     */
    private boolean securityMode = true;

    /**
     * 最大公式长度
     */
    private int maxFormulaLength = 1024;

    /**
     * 缓存配置
     */
    private Cache cache = new Cache();

    @Data
    public static class Cache {
        /**
         * 是否启用缓存
         */
        private boolean enabled = true;

        /**
         * 缓存最大大小
         */
        private int maxSize = 1000;

        /**
         * 缓存过期时间（分钟）
         */
        private int expireMinutes = 30;
    }
}

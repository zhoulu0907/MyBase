package com.cmsr.onebase.module.flow.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 环境认证方式枚举
 * <p>
 * 定义连接器环境配置支持的认证方式类型
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Getter
@AllArgsConstructor
public enum EnvAuthTypeEnum {

    /**
     * 无认证
     */
    NONE("NONE", "无认证"),

    /**
     * 基础认证（用户名/密码）
     */
    BASIC("BASIC", "基础认证"),

    /**
     * Token认证
     */
    TOKEN("TOKEN", "Token认证"),

    /**
     * Bearer Token认证
     */
    BEARER("BEARER", "Bearer Token认证"),

    /**
     * OAuth2认证
     */
    OAUTH2("OAUTH2", "OAuth2认证"),

    /**
     * API Key认证
     */
    API_KEY("API_KEY", "API Key认证"),

    /**
     * JWT认证
     */
    JWT("JWT", "JWT认证"),

    /**
     * 自定义认证
     */
    CUSTOM("CUSTOM", "自定义认证");

    /**
     * 认证方式编码
     */
    private final String code;

    /**
     * 认证方式描述
     */
    private final String description;
}

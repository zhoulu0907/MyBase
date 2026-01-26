package com.cmsr.onebase.module.flow.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 环境编码枚举
 * <p>
 * 定义连接器环境配置的标准环境编码
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Getter
@AllArgsConstructor
public enum EnvCodeEnum {

    /**
     * 开发环境
     */
    DEV("DEV", "开发环境"),

    /**
     * 测试环境
     */
    TEST("TEST", "测试环境"),

    /**
     * 用户验收测试环境
     */
    UAT("UAT", "用户验收测试环境"),

    /**
     * 预发布环境
     */
    STAGING("STAGING", "预发布环境"),

    /**
     * 生产环境
     */
    PROD("PROD", "生产环境"),

    /**
     * 自定义环境
     */
    CUSTOM("CUSTOM", "自定义环境");

    /**
     * 环境编码
     */
    private final String code;

    /**
     * 环境描述
     */
    private final String description;
}

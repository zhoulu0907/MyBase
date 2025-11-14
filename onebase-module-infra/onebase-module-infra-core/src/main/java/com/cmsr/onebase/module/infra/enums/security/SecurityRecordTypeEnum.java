package com.cmsr.onebase.module.infra.enums.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 安全记录类型枚举
 *
 * @author matianyu
 * @date 2025-11-12
 */
@Getter
@AllArgsConstructor
public enum SecurityRecordTypeEnum {

    /**
     * 密码历史记录
     */
    PASSWORD_HISTORY("PASSWORD_HISTORY", "密码历史记录");

    /**
     * 记录类型代码
     */
    private final String code;

    /**
     * 记录类型描述
     */
    private final String description;

}

package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字段约束配置类型。
 */
@Getter
@AllArgsConstructor
public enum MetadataFieldConstraintTypeEnum {

    LENGTH_RANGE("LENGTH_RANGE", "长度范围"),
    REGEX("REGEX", "正则表达式");

    private final String code;
    private final String description;
}

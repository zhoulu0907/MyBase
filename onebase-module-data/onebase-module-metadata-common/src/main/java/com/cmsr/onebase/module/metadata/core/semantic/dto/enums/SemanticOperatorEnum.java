package com.cmsr.onebase.module.metadata.core.semantic.dto.enums;

public enum SemanticOperatorEnum {
    EQ,
    NE,
    GT,
    GE,
    LT,
    LE,
    LIKE,
    IN,
    NIN,

    // 以下为补充内容

    // 不相似
    NOT_LIKE,
    // 为空
    EMPTY,
    // 不为空
    NOT_EMPTY,
    // 包含全部，要求所有元素均存在
    CONTAINS_ALL,
    // 不包含全部，要求所有元素均不存在
    NOT_CONTAINS_ALL,
    // 任一元素存在即可
    CONTAINS_ANY,
    // 任一元素均不可存在
    NOT_CONTAINS_ANY
}


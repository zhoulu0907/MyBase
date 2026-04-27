package com.cmsr.onebase.module.metadata.core.service.datamethod.strategy;

/**
 * 字段值转换模式
 *
 * @author matianyu
 * @date 2025-11-15
 */
public enum FieldValueTransformMode {
    /**
     * 写入数据库前的转换
     */
    STORE,
    /**
     * 查询返回结果时的转换
     */
    READ
}


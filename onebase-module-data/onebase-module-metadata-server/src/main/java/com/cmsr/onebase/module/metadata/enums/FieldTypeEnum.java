package com.cmsr.onebase.module.metadata.enums;

import lombok.Getter;

/**
 * 字段类型枚举
 *
 * @author bty418
 * @date 2025-01-25
 */
@Getter
public enum FieldTypeEnum {

    VARCHAR("VARCHAR", "短文本", "TEXT", true, false, 255, 4000),
    TEXT("TEXT", "长文本", "TEXT", false, false, null, null),
    INTEGER("INTEGER", "整数", "NUMBER", true, false, 11, null),
    BIGINT("BIGINT", "长整数", "NUMBER", true, false, 20, null),
    DECIMAL("DECIMAL", "小数", "NUMBER", true, true, 10, null),
    DATE("DATE", "日期", "DATETIME", false, false, null, null),
    DATETIME("DATETIME", "日期时间", "DATETIME", false, false, null, null),
    TIME("TIME", "时间", "DATETIME", false, false, null, null),
    BOOLEAN("BOOLEAN", "布尔值", "BOOLEAN", false, false, null, null),
    JSON("JSON", "JSON对象", "TEXT", false, false, null, null),
    ENUM("ENUM", "枚举", "TEXT", false, false, null, null),
    PICKLIST("PICKLIST", "单选列表", "SELECT", false, false, null, null),
    SINGLE_SELECT("SINGLE_SELECT", "单选", "SELECT", false, false, null, null),
    MULTI_SELECT("MULTI_SELECT", "多选", "SELECT", false, false, null, null);

    /**
     * 字段类型编码
     */
    private final String fieldType;
    
    /**
     * 显示名称
     */
    private final String displayName;
    
    /**
     * 分类
     */
    private final String category;
    
    /**
     * 是否支持长度设置
     */
    private final Boolean supportLength;
    
    /**
     * 是否支持小数位设置
     */
    private final Boolean supportDecimal;
    
    /**
     * 默认长度
     */
    private final Integer defaultLength;
    
    /**
     * 最大长度
     */
    private final Integer maxLength;

    /**
     * 构造函数
     *
     * @param fieldType 字段类型编码
     * @param displayName 显示名称
     * @param category 分类
     * @param supportLength 是否支持长度设置
     * @param supportDecimal 是否支持小数位设置
     * @param defaultLength 默认长度
     * @param maxLength 最大长度
     */
    FieldTypeEnum(String fieldType, String displayName, String category, Boolean supportLength, 
                  Boolean supportDecimal, Integer defaultLength, Integer maxLength) {
        this.fieldType = fieldType;
        this.displayName = displayName;
        this.category = category;
        this.supportLength = supportLength;
        this.supportDecimal = supportDecimal;
        this.defaultLength = defaultLength;
        this.maxLength = maxLength;
    }

    /**
     * 根据编码获取枚举
     *
     * @param fieldType 字段类型编码
     * @return 字段类型枚举
     */
    public static FieldTypeEnum getByFieldType(String fieldType) {
        for (FieldTypeEnum type : values()) {
            if (type.getFieldType().equals(fieldType)) {
                return type;
            }
        }
        return null;
    }
} 
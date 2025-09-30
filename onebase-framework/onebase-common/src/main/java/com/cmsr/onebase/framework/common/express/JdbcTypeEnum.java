package com.cmsr.onebase.framework.common.express;

import lombok.Getter;

/**
 * JDBC类型枚举
 *
 * @Author：huangjie
 * @Date：2025/9/29 16:52
 */
@Getter
public enum JdbcTypeEnum {

    // 从FieldTypeEnum中提取的不重复JDBC类型
    BIGINT("BIGINT"),
    BOOLEAN("BOOLEAN"),
    DATE("DATE"),
    DECIMAL("DECIMAL"),
    LONGVARCHAR("LONGVARCHAR"),
    NUMERIC("NUMERIC"),
    TIMESTAMP("TIMESTAMP"),
    VARCHAR("VARCHAR");

    private final String value;

    JdbcTypeEnum(String value) {
        this.value = value;
    }

    /**
     * 根据字符串值获取对应的枚举实例
     *
     * @param value JDBC类型字符串值
     * @return 对应的枚举实例
     * @throws IllegalArgumentException 如果提供的值不存在对应的枚举实例
     */
    public static JdbcTypeEnum fromValue(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("JDBC类型值不能为空");
        }
        for (JdbcTypeEnum type : values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("无效的JDBC类型值: " + value);
    }
}

//package com.cmsr.onebase.module.flow.context.enums;
//
//import lombok.Getter;
//import org.apache.commons.lang3.StringUtils;
//
///**
// * JDBC类型枚举
// *
// * @Author：huangjie
// * @Date：2025/9/29 16:52
// */
//@Getter
//public enum JdbcTypeEnum {
//
//    // 从FieldTypeEnum中提取的不重复JDBC类型
//    BIGINT("BIGINT"),
//    BOOLEAN("BOOLEAN"),
//    DATE("DATE"),
//    DECIMAL("DECIMAL"),
//    LONGVARCHAR("LONGVARCHAR"),
//    NUMERIC("NUMERIC"),
//    TIMESTAMP("TIMESTAMP"),
//    VARCHAR("VARCHAR");
//
//    private final String code;
//
//    JdbcTypeEnum(String code) {
//        this.code = code;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    /**
//     * 根据字符串值获取对应的枚举实例
//     *
//     * @param code JDBC类型字符串值
//     * @return 对应的枚举实例
//     * @throws IllegalArgumentException 如果提供的值不存在对应的枚举实例
//     */
//    public static JdbcTypeEnum getByCode(String code) {
//        if (StringUtils.isEmpty(code)) {
//            return null;
//        }
//        for (JdbcTypeEnum type : values()) {
//            if (type.getCode().equalsIgnoreCase(code)) {
//                return type;
//            }
//        }
//        throw new IllegalArgumentException("Invalid JdbcTypeEnum code: " + code);
//    }
//}

//package com.cmsr.onebase.module.flow.context.enums;
//
//import lombok.Getter;
//import org.apache.commons.lang3.StringUtils;
//
///**
// * 字段类型枚举
// *
// * @Author：huangjie
// * @Date：2025/9/29 16:16
// */
//@Getter
//public enum FieldTypeEnum {
//
//    // 业务类型、描述、jdbc类型
//    GEOGRAPHY("地理位置", JdbcTypeEnum.LONGVARCHAR),
//    ID("唯一标识", JdbcTypeEnum.BIGINT),
//    DEPARTMENT("部门引用", JdbcTypeEnum.BIGINT),
//    DATA_SELECTION("数据选择", JdbcTypeEnum.VARCHAR),
//    RELATION("关联关系", JdbcTypeEnum.VARCHAR),
//    EMAIL("邮箱地址", JdbcTypeEnum.VARCHAR),
//    PHONE("电话号码", JdbcTypeEnum.VARCHAR),
//    URL("网址链接", JdbcTypeEnum.VARCHAR),
//    ADDRESS("详细地址", JdbcTypeEnum.LONGVARCHAR),
//    NUMBER("通用数字", JdbcTypeEnum.NUMERIC),
//    CURRENCY("货币金额", JdbcTypeEnum.DECIMAL),
//    DATE("日期", JdbcTypeEnum.DATE),
//    DATETIME("日期时间", JdbcTypeEnum.TIMESTAMP),
//    AUTO_CODE("自动编号", JdbcTypeEnum.VARCHAR),
//    FILE("文件", JdbcTypeEnum.LONGVARCHAR),
//    PASSWORD("密码", JdbcTypeEnum.VARCHAR),
//    ENCRYPTED("加密字段", JdbcTypeEnum.VARCHAR),
//    AGGREGATE("聚合统计", JdbcTypeEnum.NUMERIC),
//    LONG_TEXT("长文本", JdbcTypeEnum.LONGVARCHAR),
//    TEXT("常规文本", JdbcTypeEnum.VARCHAR),
//    BOOLEAN("布尔值", JdbcTypeEnum.BOOLEAN),
//    USER("用户引用", JdbcTypeEnum.BIGINT),
//    STRUCTURE("结构化对象", JdbcTypeEnum.LONGVARCHAR),
//    IMAGE("图片", JdbcTypeEnum.LONGVARCHAR),
//    SELECT("单选列表", JdbcTypeEnum.VARCHAR),
//    ARRAY("数组列表", JdbcTypeEnum.LONGVARCHAR),
//    MULTI_SELECT("多选列表", JdbcTypeEnum.LONGVARCHAR),
//    MULTI_USER("用户多选", JdbcTypeEnum.LONGVARCHAR),
//    MULTI_DEPARTMENT("部门多选", JdbcTypeEnum.LONGVARCHAR),
//    MULTI_DATA_SELECTION("数据多选", JdbcTypeEnum.LONGVARCHAR);
//
//    private final String description;
//    private final JdbcTypeEnum jdbcType;
//
//    FieldTypeEnum(String description, JdbcTypeEnum jdbcType) {
//        this.description = description;
//        this.jdbcType = jdbcType;
//    }
//
//    public static FieldTypeEnum getByName(String name) {
//        for (FieldTypeEnum typeEnum : values()) {
//            if (typeEnum.name().equalsIgnoreCase(name)) {
//                return typeEnum;
//            }
//        }
//        return null;
//    }
//
//}

package com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cmsr.onebase.module.metadata.runtime.semantic.type.DataSelectRefType;
import com.cmsr.onebase.module.metadata.runtime.semantic.type.DeptRefType;
import com.cmsr.onebase.module.metadata.runtime.semantic.type.DictSelectRefType;
import com.cmsr.onebase.module.metadata.runtime.semantic.type.FileRefType;
import com.cmsr.onebase.module.metadata.runtime.semantic.type.ImageRefType;
import com.cmsr.onebase.module.metadata.runtime.semantic.type.RefType;
import com.cmsr.onebase.module.metadata.runtime.semantic.type.UserRefType;

/**
 * 字段类型枚举
 * 定义所有业务字段类型及其对应的Java类型
 *
 * @author matianyu
 * @date 2025-11-25
 */
@Getter
@AllArgsConstructor
public enum SemanticFieldTypeEnum {

    // ==================== 文本类型 ====================
    /** 常规文本 */
    TEXT("TEXT", "常规文本", String.class, String.class, false),
    /** 长文本 */
    LONG_TEXT("LONG_TEXT", "长文本", String.class, String.class, false),
    /** 邮箱地址 */
    EMAIL("EMAIL", "邮箱地址", String.class, String.class, false),
    /** 电话号码 */
    PHONE("PHONE", "电话号码", String.class, String.class, false),
    /** 网址链接 */
    URL("URL", "网址链接", String.class, String.class, false),
    /** 详细地址 */
    ADDRESS("ADDRESS", "详细地址", String.class, String.class, false),
    /** 自动编号 */
    AUTO_CODE("AUTO_CODE", "自动编号", String.class, String.class, false),
    /** 密码 */
    PASSWORD("PASSWORD", "密码", String.class, String.class, false),
    /** 加密字段 */
    ENCRYPTED("ENCRYPTED", "加密字段", String.class, String.class, false),

    // ==================== 数值类型 ====================
    /** 通用数字 */
    NUMBER("NUMBER", "通用数字", BigDecimal.class, BigDecimal.class, false),
    /** 聚合统计 */
    AGGREGATE("AGGREGATE", "聚合统计", BigDecimal.class, BigDecimal.class, false),
    /** 数据标识ID */
    ID("ID", "数据标识", Long.class, Long.class, false),

    // ==================== 日期时间类型 ====================
    /** 日期 */
    DATE("DATE", "日期", LocalDate.class, LocalDate.class, false),
    /** 日期时间 */
    DATETIME("DATETIME", "日期时间", LocalDateTime.class, LocalDateTime.class, false),

    // ==================== 布尔类型 ====================
    /** 布尔值 */
    BOOLEAN("BOOLEAN", "布尔值", Boolean.class, Boolean.class, false),

    // ==================== 单选类型（返回单个值或ID） ====================
    /** 单选列表 */
    SELECT("SELECT", "单选列表", String.class, DictSelectRefType.class, false),
    /** 用户单选 */
    USER("USER", "用户单选", Long.class, UserRefType.class, false),
    /** 部门单选 */
    DEPARTMENT("DEPARTMENT", "部门单选", Long.class, DeptRefType.class, false),
    /** 数据单选 */
    DATA_SELECTION("DATA_SELECTION", "数据单选", Long.class, DataSelectRefType.class, false),

    // ==================== 多选类型（返回列表） ====================
    /** 多选列表 */
    MULTI_SELECT("MULTI_SELECT", "多选列表", String.class, DictSelectRefType.class, true),
    /** 用户多选 */
    MULTI_USER("MULTI_USER", "用户多选", String.class, UserRefType.class, true),
    /** 部门多选 */
    MULTI_DEPARTMENT("MULTI_DEPARTMENT", "部门多选", String.class, DeptRefType.class, true),
    /** 数据多选 */
    MULTI_DATA_SELECTION("MULTI_DATA_SELECTION", "数据多选", String.class, DataSelectRefType.class, true),

    // ==================== 复杂类型 ====================
    /** 文件 */
    FILE("FILE", "文件", String.class, FileRefType.class, true),
    /** 图片 */
    IMAGE("IMAGE", "图片", String.class, ImageRefType.class, true),
    /** 地理位置 */
    GEOGRAPHY("GEOGRAPHY", "地理位置", String.class, String.class,  false);

    /** 字段类型编码 */
    private final String code;
    /** 字段类型名称 */
    private final String name;
    /** 对应的Java类型 */
    private final Class<?> rawJavaType;
    /** 对应的Java类型 */
    private final Class<?> bizJavaType;
    /** 是否是列表类型 */
    private final boolean listType;

    /**
     * 根据编码获取枚举
     *
     * @param code 字段类型编码
     * @return 对应的枚举，未找到返回null
     */
    public static SemanticFieldTypeEnum ofCode(String code) {
        if (code == null) {
            return null;
        }
        for (SemanticFieldTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否是文本类型
     */
    public boolean isStringType() {
        return this == TEXT || this == LONG_TEXT || this == EMAIL || this == PHONE
                || this == URL || this == ADDRESS || this == AUTO_CODE;
    }

    /**
     * 判断是否是数值类型
     */
    public boolean isNumberType() {
        return this == NUMBER || this == AGGREGATE || this == ID;
    }

    /**
     * 判断是否是日期时间类型
     */
    public boolean isDateType() {
        return this == DATE || this == DATETIME;
    }

    /**
     * 判断是否是ID引用类型（用户/部门/数据选择）
     */
    public boolean isRefType() {
        return RefType.class.isAssignableFrom(this.bizJavaType);
    }
}

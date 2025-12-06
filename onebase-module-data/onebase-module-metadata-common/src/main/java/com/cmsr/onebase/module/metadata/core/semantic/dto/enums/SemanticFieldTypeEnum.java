package com.cmsr.onebase.module.metadata.core.semantic.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.cmsr.onebase.module.metadata.core.semantic.type.DataSelectRefType;
import com.cmsr.onebase.module.metadata.core.semantic.type.DeptRefType;
import com.cmsr.onebase.module.metadata.core.semantic.type.DictSelectRefType;
import com.cmsr.onebase.module.metadata.core.semantic.type.FileRefType;
import com.cmsr.onebase.module.metadata.core.semantic.type.ImageRefType;
import com.cmsr.onebase.module.metadata.core.semantic.type.RefType;
import com.cmsr.onebase.module.metadata.core.semantic.type.UserRefType;

@Getter
@AllArgsConstructor
public enum SemanticFieldTypeEnum {

    TEXT("TEXT", "常规文本", String.class, String.class, false),
    LONG_TEXT("LONG_TEXT", "长文本", String.class, String.class, false),
    EMAIL("EMAIL", "邮箱地址", String.class, String.class, false),
    PHONE("PHONE", "电话号码", String.class, String.class, false),
    URL("URL", "网址链接", String.class, String.class, false),
    ADDRESS("ADDRESS", "详细地址", String.class, String.class, false),
    AUTO_CODE("AUTO_CODE", "自动编号", String.class, String.class, false),
    PASSWORD("PASSWORD", "密码", String.class, String.class, false),
    ENCRYPTED("ENCRYPTED", "加密字段", String.class, String.class, false),

    NUMBER("NUMBER", "通用数字", BigDecimal.class, BigDecimal.class, false),
    AGGREGATE("AGGREGATE", "聚合统计", BigDecimal.class, BigDecimal.class, false),
    ID("ID", "数据标识", Long.class, Long.class, false),

    DATE("DATE", "日期", LocalDate.class, LocalDate.class, false),
    DATETIME("DATETIME", "日期时间", LocalDateTime.class, LocalDateTime.class, false),

    BOOLEAN("BOOLEAN", "布尔值", Boolean.class, Boolean.class, false),

    SELECT("SELECT", "单选列表", String.class, DictSelectRefType.class, false),
    USER("USER", "用户单选", Long.class, UserRefType.class, false),
    DEPARTMENT("DEPARTMENT", "部门单选", Long.class, DeptRefType.class, false),
    DATA_SELECTION("DATA_SELECTION", "数据单选", Long.class, DataSelectRefType.class, false),

    MULTI_SELECT("MULTI_SELECT", "多选列表", String.class, DictSelectRefType.class, true),
    MULTI_USER("MULTI_USER", "用户多选", String.class, UserRefType.class, true),
    MULTI_DEPARTMENT("MULTI_DEPARTMENT", "部门多选", String.class, DeptRefType.class, true),
    MULTI_DATA_SELECTION("MULTI_DATA_SELECTION", "数据多选", String.class, DataSelectRefType.class, true),

    FILE("FILE", "文件", String.class, FileRefType.class, true),
    IMAGE("IMAGE", "图片", String.class, ImageRefType.class, true),
    GEOGRAPHY("GEOGRAPHY", "地理位置", String.class, String.class,  false);

    private final String code;
    private final String name;
    private final Class<?> rawJavaType;
    private final Class<?> bizJavaType;
    private final boolean listType;

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

    public boolean isStringType() {
        return this == TEXT || this == LONG_TEXT || this == EMAIL || this == PHONE
                || this == URL || this == ADDRESS || this == AUTO_CODE;
    }

    public boolean isNumberType() {
        return this == NUMBER || this == AGGREGATE || this == ID;
    }

    public boolean isDateType() {
        return this == DATE || this == DATETIME;
    }

    public boolean isRefType() {
        return RefType.class.isAssignableFrom(this.bizJavaType);
    }
}

package com.cmsr.onebase.module.metadata.core.enums;

import lombok.Getter;

/**
 * 级联操作类型枚举
 *
 * @author bty418
 * @date 2025-01-25
 */
@Getter
public enum CascadeTypeEnum {

    READ("read", "只读关联", "仅支持读取关联数据，不进行任何级联操作"),
    ALL("all", "全级联", "支持所有级联操作（增删改查）"),
    DELETE("delete", "级联删除", "删除主记录时级联删除关联记录"),
    NONE("none", "无级联", "不进行任何级联操作");

    /**
     * 级联类型编码
     */
    private final String cascadeType;

    /**
     * 显示名称
     */
    private final String displayName;

    /**
     * 描述信息
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param cascadeType 级联类型编码
     * @param displayName 显示名称
     * @param description 描述信息
     */
    CascadeTypeEnum(String cascadeType, String displayName, String description) {
        this.cascadeType = cascadeType;
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 根据编码获取枚举
     *
     * @param cascadeType 级联类型编码
     * @return 级联类型枚举
     */
    public static CascadeTypeEnum getByType(String cascadeType) {
        for (CascadeTypeEnum type : values()) {
            if (type.getCascadeType().equals(cascadeType)) {
                return type;
            }
        }
        return null;
    }
}

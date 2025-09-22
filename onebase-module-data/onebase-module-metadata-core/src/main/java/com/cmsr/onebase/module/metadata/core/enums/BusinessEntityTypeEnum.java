package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务实体类型枚举
 *
 * @author matianyu
 * @date 2025-08-06
 */
@Getter
@AllArgsConstructor
public enum BusinessEntityTypeEnum {

    /**
     * 自建表
     */
    SELF_BUILT(1, "自建表", "创建新的物理表，由系统管理表结构"),

    /**
     * 复用已有表
     */
    REUSE_EXISTING(2, "复用已有表", "使用数据源中已存在的表，不修改表结构"),

    /**
     * 中间表
     */
    MIDDLE_TABLE(3, "中间表", "用于多对多关联的中间表，不对前端展示");

    /**
     * 类型值
     */
    private final Integer code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据code获取枚举
     *
     * @param code 类型代码
     * @return 对应的枚举，如果不存在则返回null
     */
    public static BusinessEntityTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (BusinessEntityTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为有效的类型代码
     *
     * @param code 类型代码
     * @return 是否有效
     */
    public static boolean isValidCode(Integer code) {
        return getByCode(code) != null;
    }

    /**
     * 判断是否需要创建物理表
     * 只有自建表类型需要创建物理表
     *
     * @param entityType 实体类型
     * @return 是否需要创建物理表
     */
    public static boolean needCreatePhysicalTable(Integer entityType) {
        BusinessEntityTypeEnum type = getByCode(entityType);
        return type == SELF_BUILT;
    }

    /**
     * 判断是否对前端可见
     * 中间表不对前端展示
     *
     * @param entityType 实体类型
     * @return 是否对前端可见
     */
    public static boolean isVisibleToFrontend(Integer entityType) {
        BusinessEntityTypeEnum type = getByCode(entityType);
        return type != MIDDLE_TABLE;
    }

    /**
     * 判断是否允许修改表结构
     * 只有自建表和中间表允许修改表结构
     *
     * @param entityType 实体类型
     * @return 是否允许修改表结构
     */
    public static boolean allowModifyTableStructure(Integer entityType) {
        BusinessEntityTypeEnum type = getByCode(entityType);
        return type == SELF_BUILT || type == MIDDLE_TABLE;
    }
}

package com.cmsr.onebase.module.metadata.core.enums;

import lombok.Getter;

/**
 * 关系类型枚举
 *
 * @author bty418
 * @date 2025-01-25
 */
@Getter
public enum RelationshipTypeEnum {

    ONE_TO_ONE("ONE_TO_ONE", "一对一", "一对一关联关系"),
    ONE_TO_MANY("ONE_TO_MANY", "一对多", "一对多关联关系"),
    MANY_TO_ONE("MANY_TO_ONE", "多对一", "多对一关联关系"),
    MANY_TO_MANY("MANY_TO_MANY", "多对多", "多对多关联关系");

    /**
     * 关系类型编码
     */
    private final String relationshipType;

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
     * @param relationshipType 关系类型编码
     * @param displayName 显示名称
     * @param description 描述信息
     */
    RelationshipTypeEnum(String relationshipType, String displayName, String description) {
        this.relationshipType = relationshipType;
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * 根据编码获取枚举
     *
     * @param relationshipType 关系类型编码
     * @return 关系类型枚举
     */
    public static RelationshipTypeEnum getByType(String relationshipType) {
        for (RelationshipTypeEnum type : values()) {
            if (type.getRelationshipType().equals(relationshipType)) {
                return type;
            }
        }
        return null;
    }
}

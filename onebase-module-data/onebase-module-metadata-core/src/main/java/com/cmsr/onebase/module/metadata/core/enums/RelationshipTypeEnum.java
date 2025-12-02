package com.cmsr.onebase.module.metadata.core.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 关系类型枚举
 * 定义实体之间的关联关系类型，包括自定义关联关系、主子表关系、数据选择关系
 *
 * @author bty418
 * @date 2025-01-25
 */
@Getter
public enum RelationshipTypeEnum {

    // ==================== 自定义关联关系 ====================
    /**
     * 自定义一对一关系
     */
    DEFINE_ONE_TO_ONE("DEFINE_ONE_TO_ONE", "自定义一对一", "用户自定义的一对一关联关系"),

    /**
     * 自定义一对多关系
     */
    DEFINE_ONE_TO_MANY("DEFINE_ONE_TO_MANY", "自定义一对多", "用户自定义的一对多关联关系"),

    /**
     * 自定义多对一关系
     */
    DEFINE_MANY_TO_ONE("DEFINE_MANY_TO_ONE", "自定义多对一", "用户自定义的多对一关联关系"),

    /**
     * 自定义多对多关系
     */
    DEFINE_MANY_TO_MANY("DEFINE_MANY_TO_MANY", "自定义多对多", "用户自定义的多对多关联关系"),

    // ==================== 主子表关系 ====================
    /**
     * 主子表一对多关系
     */
    SUBTABLE_ONE_TO_MANY("SUBTABLE_ONE_TO_MANY", "主子表", "主子表一对多关系，主表通过id关联子表的parent_id"),

    // ==================== 数据选择关系 ====================
    /**
     * 数据选择（单选）
     */
    DATA_SELECT("DATA_SELECT", "数据选择", "数据选择单选关系，通过字段关联其他实体的数据"),

    /**
     * 数据选择（多选）
     */
    DATA_SELECT_MULTI("DATA_SELECT_MULTI", "数据选择多选", "数据选择多选关系，通过字段关联其他实体的多条数据");

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
     * @return 关系类型枚举，不存在则返回null
     */
    public static RelationshipTypeEnum getByType(String relationshipType) {
        if (relationshipType == null || relationshipType.isBlank()) {
            return null;
        }
        for (RelationshipTypeEnum type : values()) {
            if (type.getRelationshipType().equals(relationshipType)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为有效的关系类型编码
     *
     * @param relationshipType 关系类型编码
     * @return 是否有效
     */
    public static boolean isValidType(String relationshipType) {
        return getByType(relationshipType) != null;
    }

    /**
     * 获取所有关系类型编码
     *
     * @return 所有关系类型编码数组
     */
    public static String[] getAllTypes() {
        return Arrays.stream(values())
                .map(RelationshipTypeEnum::getRelationshipType)
                .toArray(String[]::new);
    }

    /**
     * 判断是否为自定义关联关系类型
     *
     * @param relationshipType 关系类型编码
     * @return 是否为自定义关联关系
     */
    public static boolean isDefineRelationship(String relationshipType) {
        RelationshipTypeEnum type = getByType(relationshipType);
        return type == DEFINE_ONE_TO_ONE || type == DEFINE_ONE_TO_MANY
                || type == DEFINE_MANY_TO_ONE || type == DEFINE_MANY_TO_MANY;
    }

    /**
     * 判断是否为主子表关系类型
     *
     * @param relationshipType 关系类型编码
     * @return 是否为主子表关系
     */
    public static boolean isSubtableRelationship(String relationshipType) {
        RelationshipTypeEnum type = getByType(relationshipType);
        return type == SUBTABLE_ONE_TO_MANY;
    }

    /**
     * 判断是否为数据选择关系类型
     *
     * @param relationshipType 关系类型编码
     * @return 是否为数据选择关系
     */
    public static boolean isDataSelectRelationship(String relationshipType) {
        RelationshipTypeEnum type = getByType(relationshipType);
        return type == DATA_SELECT || type == DATA_SELECT_MULTI;
    }

    /**
     * 判断是否为一对一类型的关系（包括自定义一对一和数据选择单选）
     *
     * @param relationshipType 关系类型编码
     * @return 是否为一对一类型
     */
    public static boolean isOneToOneType(String relationshipType) {
        RelationshipTypeEnum type = getByType(relationshipType);
        return type == DEFINE_ONE_TO_ONE || type == DATA_SELECT;
    }

    /**
     * 判断是否为一对多类型的关系（包括自定义一对多、主子表）
     *
     * @param relationshipType 关系类型编码
     * @return 是否为一对多类型
     */
    public static boolean isOneToManyType(String relationshipType) {
        RelationshipTypeEnum type = getByType(relationshipType);
        return type == DEFINE_ONE_TO_MANY || type == SUBTABLE_ONE_TO_MANY;
    }

    /**
     * 判断是否为多对一类型的关系
     *
     * @param relationshipType 关系类型编码
     * @return 是否为多对一类型
     */
    public static boolean isManyToOneType(String relationshipType) {
        RelationshipTypeEnum type = getByType(relationshipType);
        return type == DEFINE_MANY_TO_ONE;
    }

    /**
     * 判断是否为多对多类型的关系（包括自定义多对多、数据选择多选）
     *
     * @param relationshipType 关系类型编码
     * @return 是否为多对多类型
     */
    public static boolean isManyToManyType(String relationshipType) {
        RelationshipTypeEnum type = getByType(relationshipType);
        return type == DEFINE_MANY_TO_MANY || type == DATA_SELECT_MULTI;
    }
}

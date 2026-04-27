package com.cmsr.onebase.module.metadata.core.semantic.dto.enums;

import lombok.Getter;

@Getter
public enum SemanticRelationshipTypeEnum {
    DEFINE_ONE_TO_ONE("DEFINE_ONE_TO_ONE", "自定义一对一"),
    DEFINE_ONE_TO_MANY("DEFINE_ONE_TO_MANY", "自定义一对多"),
    DEFINE_MANY_TO_ONE("DEFINE_MANY_TO_ONE", "自定义多对一"),
    DEFINE_MANY_TO_MANY("DEFINE_MANY_TO_MANY", "自定义多对多"),
    SUBTABLE_ONE_TO_MANY("SUBTABLE_ONE_TO_MANY", "主子表"),
    DATA_SELECT("DATA_SELECT", "数据选择"),
    DATA_SELECT_MULTI("DATA_SELECT_MULTI", "数据选择多选");

    private final String relationshipType;
    private final String displayName;

    SemanticRelationshipTypeEnum(String relationshipType, String displayName) {
        this.relationshipType = relationshipType;
        this.displayName = displayName;
    }
}


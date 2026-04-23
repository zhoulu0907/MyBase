package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 实体在关系中的角色编码。
 */
@Getter
@AllArgsConstructor
public enum MetadataEntityRelationRoleEnum {

    PARENT("PARENT", "主表"),
    CHILD("CHILD", "子表"),
    MASTER("MASTER", "主表"),
    SLAVE("SLAVE", "子表"),
    NONE("NONE", "无关系");

    private final String code;
    private final String description;
}

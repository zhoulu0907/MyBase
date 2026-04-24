package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 实体关系级联类型编码。
 */
@Getter
@AllArgsConstructor
public enum MetadataRelationshipCascadeTypeEnum {

    READ("READ", "只读关联");

    private final String code;
    private final String description;
}

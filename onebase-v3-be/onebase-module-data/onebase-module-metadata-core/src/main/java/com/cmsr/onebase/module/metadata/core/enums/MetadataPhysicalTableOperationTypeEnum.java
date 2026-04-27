package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物理表字段操作类型。
 */
@Getter
@AllArgsConstructor
public enum MetadataPhysicalTableOperationTypeEnum {

    ADD("ADD", "新增列"),
    ALTER("ALTER", "修改列"),
    DROP("DROP", "删除列"),
    RENAME("RENAME", "重命名列");

    private final String code;
    private final String description;
}

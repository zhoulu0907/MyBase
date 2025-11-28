package com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "连接器类型枚举")
/**
 * 连接器类型枚举
 *
 * <p>RELATION 表示普通实体关系；SUBTABLE 表示多对多中间表（子表）。</p>
 */
public enum ConnectorTypeEnum {
    RELATION,
    SUBTABLE
}

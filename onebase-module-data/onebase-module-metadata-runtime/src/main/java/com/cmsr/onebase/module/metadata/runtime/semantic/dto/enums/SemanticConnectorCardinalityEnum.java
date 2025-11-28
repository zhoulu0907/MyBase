package com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "连接器基数枚举")
/**
 * 连接器基数枚举
 *
 * <p>表示连接的基数：ONE（单行）或 MANY（多行）。</p>
 */
public enum SemanticConnectorCardinalityEnum {
    ONE,
    MANY
}

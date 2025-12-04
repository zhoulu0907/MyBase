package com.cmsr.onebase.module.metadata.core.semantic.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "连接器基数枚举")
public enum SemanticConnectorCardinalityEnum {
    ONE,
    MANY
}

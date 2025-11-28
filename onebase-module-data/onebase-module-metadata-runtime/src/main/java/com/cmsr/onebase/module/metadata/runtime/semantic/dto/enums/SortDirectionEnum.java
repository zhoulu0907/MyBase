package com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "排序方向枚举")
/**
 * 排序方向枚举
 *
 * <p>ASC 升序；DESC 降序。</p>
 */
public enum SortDirectionEnum {
    ASC,
    DESC
}

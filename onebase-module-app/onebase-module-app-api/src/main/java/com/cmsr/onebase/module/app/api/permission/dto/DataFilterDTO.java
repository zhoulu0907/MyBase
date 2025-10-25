package com.cmsr.onebase.module.app.api.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/10/24 17:47
 */
@Data
public class DataFilterDTO {

    @Schema(description = "数据权Id")
    private Long id;
    /**
     * 数据权限组Id
     */
    @Schema(description = "字段id")
    private Long fieldId;

    /**
     * 字段值类型
     */
    @Schema(description = "字段值类型")
    private String fieldValueType;

    /**
     * 比较操作符号
     */
    @Schema(description = "比较操作符号")
    private String fieldOperator;

    /**
     * 字段值
     */
    @Schema(description = "字段值")
    private String fieldValue;
}

package com.cmsr.onebase.module.metadata.api.validation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限参考操作类型响应 DTO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Schema(description = "权限参考操作类型响应 DTO")
@Data
public class PermitRefOtftRespDTO {

    @Schema(description = "操作类型ID", example = "1")
    private Long id;

    @Schema(description = "操作类型代码", example = "READ")
    private String operationTypeCode;

    @Schema(description = "操作类型名称", example = "查看")
    private String operationTypeName;

    @Schema(description = "字段类型代码", example = "STRING")
    private String fieldTypeCode;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "备注", example = "查看操作权限")
    private String remark;
}
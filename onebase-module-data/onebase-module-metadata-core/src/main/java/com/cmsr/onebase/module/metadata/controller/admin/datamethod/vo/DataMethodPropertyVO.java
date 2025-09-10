package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 数据方法属性 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 数据方法属性 VO")
@Data
public class DataMethodPropertyVO {

    @Schema(description = "字段名称", example = "id")
    private String fieldName;

    @Schema(description = "字段类型", example = "BIGINT")
    private String fieldType;

    @Schema(description = "字段描述", example = "用户ID")
    private String description;

} 
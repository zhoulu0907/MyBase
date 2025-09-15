package com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据方法参数VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Data
@Schema(description = "数据方法参数VO")
public class DataMethodParameterVO {

    @Schema(description = "参数名称")
    private String paramName;

    @Schema(description = "参数类型")
    private String paramType;

    @Schema(description = "参数描述")
    private String description;

    @Schema(description = "是否必填")
    private Boolean required;

    @Schema(description = "默认值")
    private String defaultValue;
}

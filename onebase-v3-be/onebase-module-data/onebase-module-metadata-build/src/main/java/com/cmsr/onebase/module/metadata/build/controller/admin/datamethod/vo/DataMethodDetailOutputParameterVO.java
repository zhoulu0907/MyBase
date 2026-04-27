package com.cmsr.onebase.module.metadata.build.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据方法详情输出参数VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Data
@Schema(description = "数据方法详情输出参数VO")
public class DataMethodDetailOutputParameterVO {

    @Schema(description = "输出类型")
    private String outputType;

    @Schema(description = "输出描述")
    private String description;

    @Schema(description = "输出示例")
    private String exampleValue;
}

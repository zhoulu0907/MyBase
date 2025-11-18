package com.cmsr.onebase.module.etl.build.service.mgt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FlinkFunctionVO {

    @Schema(description = "函数类型")
    private String functionType;

    @Schema(description = "函数名称")
    private String functionName;

    @Schema(description = "函数描述")
    private String functionDesc;

}

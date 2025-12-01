package com.cmsr.onebase.module.formula.vo.function;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "公式引擎 - 函数分组列表 Response VO")
@Data
public class FunctionGroupRespVo {

    @Schema(description = "函数类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "BUILT_IN")
    private String type;

    private List<FunctionRespVO> functions;

}

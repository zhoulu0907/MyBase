package com.cmsr.onebase.module.formula.vo.function;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "公式引擎 - 函数精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionSimpleRespVO {

    @Schema(description = "函数编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "函数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUM")
    private String name;

    /**
     * @see com.cmsr.onebase.framework.common.enums.FunctionTypeEnum
     */
    @Schema(description = "函数类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "TEXT/NUMBER/DATE/LOGIC/USER")
    private String type;

    @Schema(description = "函数简介", example = "求和函数")
    private String summary;

    /**
     * 函数表达式
     */
    @Schema(description = "函数公式内容", example = "SUM()")
    private String expression;


    /**
     * 函数用法（md格式）
     */
    @Schema(description = "函数用法", example = "用法")
    private String usage;

    /**
     * 函数示例（md格式）
     */
    @Schema(description = "函数示例", example = "示例")
    private String example;

    /**
     * 返回值类型（枚举Code）
     * @see com.cmsr.onebase.framework.common.enums.ReturnTypeEnum
     */
    @Schema(description = "返回类型", example = "NUMBER/TEXT/BOOLEAN")
    private String returnType;

}

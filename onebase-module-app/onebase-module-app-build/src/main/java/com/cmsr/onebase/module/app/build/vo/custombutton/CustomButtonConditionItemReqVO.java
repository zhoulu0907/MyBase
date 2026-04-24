package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-可用条件项")
public class CustomButtonConditionItemReqVO {

    @Schema(description = "字段UUID")
    private String fieldUuid;

    @Schema(description = "字段编码")
    private String fieldCode;

    @NotBlank(message = "条件操作符不能为空")
    @Schema(description = "操作符：EQ、NE、GT、GE、LT、LE、IN、NOT_IN、CONTAINS、EMPTY、NOT_EMPTY", requiredMode = Schema.RequiredMode.REQUIRED)
    private String operator;

    @NotBlank(message = "条件值类型不能为空")
    @Schema(description = "值类型：STATIC 静态值、VARIABLE 当前记录字段、FORMULA 公式", requiredMode = Schema.RequiredMode.REQUIRED)
    private String valueType;

    @Schema(description = "比较值。VARIABLE 时填写字段UUID或字段编码；FORMULA 时填写公式表达式")
    private String compareValue;

    @Schema(description = "排序号")
    private Integer sortNo;
}

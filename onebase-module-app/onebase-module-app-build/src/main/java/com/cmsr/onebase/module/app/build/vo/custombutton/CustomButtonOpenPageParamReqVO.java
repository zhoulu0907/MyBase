package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-打开页面自定义参数")
public class CustomButtonOpenPageParamReqVO {

    @NotBlank(message = "参数名称不能为空")
    @Schema(description = "参数名称。内部页面时建议使用目标页面入参编码；外链时为URL查询参数名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paramName;

    @NotBlank(message = "参数类型不能为空")
    @Schema(description = "参数类型：STATIC 静态值、VARIABLE 当前记录字段、FORMULA 公式", requiredMode = Schema.RequiredMode.REQUIRED)
    private String valueType;

    @Schema(description = "参数值。STATIC 为固定值；VARIABLE 为字段UUID或字段编码；FORMULA 为公式表达式")
    private String paramValue;

    @Schema(description = "排序号")
    private Integer sortNo;
}

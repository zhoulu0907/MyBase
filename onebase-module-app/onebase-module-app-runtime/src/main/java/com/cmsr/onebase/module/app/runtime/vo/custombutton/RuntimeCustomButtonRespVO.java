package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "运行态-自定义按钮")
public class RuntimeCustomButtonRespVO {

    @Schema(description = "按钮编码")
    private String buttonCode;

    @Schema(description = "按钮名称")
    private String buttonName;

    @Schema(description = "按钮描述")
    private String buttonDesc;

    @Schema(description = "动作类型")
    private String actionType;

    @Schema(description = "操作范围")
    private String operationScope;

    @Schema(description = "样式类型")
    private String styleType;

    @Schema(description = "颜色")
    private String colorHex;

    @Schema(description = "图标")
    private String iconCode;
}

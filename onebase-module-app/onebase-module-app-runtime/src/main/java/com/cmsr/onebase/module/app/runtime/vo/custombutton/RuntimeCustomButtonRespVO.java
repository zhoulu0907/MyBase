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

    @Schema(description = "动作类型：UPDATE_FORM、CREATE_RELATED_RECORD、TRIGGER_FLOW、OPEN_PAGE")
    private String actionType;

    @Schema(description = "操作范围：SINGLE 单条记录、BATCH 批量记录、FORM 表单视图")
    private String operationScope;

    @Schema(description = "样式类型。示例：PRIMARY、DEFAULT、LINK、WARNING")
    private String styleType;

    @Schema(description = "按钮颜色HEX值")
    private String colorHex;

    @Schema(description = "图标编码")
    private String iconCode;
}

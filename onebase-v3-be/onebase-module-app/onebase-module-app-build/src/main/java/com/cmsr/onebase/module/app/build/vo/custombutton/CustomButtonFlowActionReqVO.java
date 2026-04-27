package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-执行自动化流配置。已废弃；新接入请使用 CustomButtonTriggerFlowActionReqVO")
public class CustomButtonFlowActionReqVO {

    @Schema(description = "自动化流流程ID")
    private Long flowProcessId;

    @Schema(description = "自动化流流程UUID")
    private String flowProcessUuid;

    @Schema(description = "是否执行前二次确认：0否 1是")
    private Integer confirmRequired;

    @Schema(description = "二次确认文案")
    private String confirmText;
}

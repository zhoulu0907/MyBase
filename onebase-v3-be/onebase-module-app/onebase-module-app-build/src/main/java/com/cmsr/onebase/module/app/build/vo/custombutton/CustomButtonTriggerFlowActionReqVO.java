package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-执行自动化流动作配置")
public class CustomButtonTriggerFlowActionReqVO {

    @NotNull(message = "自动化流流程ID不能为空")
    @Schema(description = "自动化流流程ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long flowProcessId;

    @Schema(description = "自动化流流程UUID")
    private String flowProcessUuid;

    @Schema(description = "是否执行前二次确认：0否 1是；为空默认1")
    private Integer confirmRequired;

    @Schema(description = "二次确认文案；为空使用默认确认文案")
    private String confirmText;
}

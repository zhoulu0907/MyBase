package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-执行自动化流配置")
public class CustomButtonFlowActionReqVO {

    @Schema(description = "流程ID")
    private Long flowProcessId;

    @Schema(description = "流程UUID")
    private String flowProcessUuid;

    @Schema(description = "是否需要确认：0否 1是")
    private Integer confirmRequired;

    @Schema(description = "确认文案")
    private String confirmText;
}

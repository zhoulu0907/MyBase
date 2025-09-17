package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 流程管理 - 创建流程 Request VO
 */
@Data
@Schema(description = "流程管理 - 创建流程请求参数")
public class CreateFlowProcessReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应用ID", required = true, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @Schema(description = "流程名称", required = true, example = "审批流程")
    @NotBlank(message = "流程名称不能为空")
    @Size(min = 1, max = 100, message = "流程名称长度必须在1-100之间")
    private String processName;

    @Schema(description = "流程描述", example = "这是一个审批流程")
    @Size(max = 500, message = "流程描述长度不能超过500个字符")
    private String processDescription;


    @Schema(description = "触发类型", required = true, example = "manual")
    @NotBlank(message = "触发类型不能为空")
    @Size(max = 50, message = "触发类型长度不能超过50个字符")
    private String triggerType;

    @Schema(description = "触发配置，比如表单触发传入pageId")
    private Map<String, Object> triggerConfig;
}
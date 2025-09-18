package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 流程管理 - 更新流程 Request VO
 */
@Data
@Schema(description = "流程管理 - 更新流程请求参数")
public class UpdateFlowProcessReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "流程ID", required = true, example = "1")
    @NotNull(message = "流程ID不能为空")
    private Long id;

    @Schema(description = "流程名称", example = "审批流程")
    @Size(min = 1, max = 100, message = "流程名称长度必须在1-100之间")
    private String processName;

    @Schema(description = "流程状态：0-禁用，1-启用", example = "1")
    private Integer processStatus;

    @Schema(description = "流程描述", example = "这是一个审批流程")
    @Size(max = 500, message = "流程描述长度不能超过500个字符")
    private String processDescription;


}
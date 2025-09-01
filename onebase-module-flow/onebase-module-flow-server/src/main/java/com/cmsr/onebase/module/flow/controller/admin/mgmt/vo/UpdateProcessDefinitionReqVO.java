package com.cmsr.onebase.module.flow.controller.admin.mgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/1 9:55
 */
@Data
@Schema(description = "流程管理 - 更新流程定义请求参数")
public class UpdateProcessDefinitionReqVO {

    @Schema(description = "流程ID", required = true, example = "1")
    @NotNull(message = "流程ID不能为空")
    private Long id;

    @Schema(description = "流程定义", required = true)
    @NotNull(message = "流程定义不能为空")
    private String processDefinition;

}

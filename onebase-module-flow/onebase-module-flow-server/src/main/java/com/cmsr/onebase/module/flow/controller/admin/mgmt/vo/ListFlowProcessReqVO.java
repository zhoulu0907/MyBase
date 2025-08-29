package com.cmsr.onebase.module.flow.controller.admin.mgmt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

/**
 * 流程管理 - 分页查询流程 Request VO
 */
@Data
@Schema(description = "流程管理 - 分页查询流程请求参数")
public class ListFlowProcessReqVO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Schema(description = "应用ID", example = "1")
    private Long applicationId;

    @Schema(description = "流程名称", example = "审批流程")
    private String processName;

    @Schema(description = "流程状态：0-禁用，1-启用", example = "1")
    private Integer processStatus;

    @Schema(description = "触发类型", example = "manual")
    private String triggerType;

    @Schema(description = "页码", required = true, example = "1")
    private Integer pageNum;

    @Schema(description = "每页数量", required = true, example = "10")
    private Integer pageSize;
}
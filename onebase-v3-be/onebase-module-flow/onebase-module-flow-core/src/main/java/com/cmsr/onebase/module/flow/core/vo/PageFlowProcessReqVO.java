package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程管理 - 分页查询流程 Request VO
 */
@Data
@Schema(description = "流程管理 - 分页查询流程请求参数")
public class PageFlowProcessReqVO extends PageParam {
    
    private static final long serialVersionUID = 1L;

    @Schema(description = "应用ID", example = "1")
    private Long applicationId;

    @Schema(description = "流程名称", example = "审批流程")
    private String processName;

    @Schema(description = "流程状态：0-禁用，1-启用", example = "1")
    private Integer enableStatus;

    @Schema(description = "触发类型", example = "manual")
    private String triggerType;
}
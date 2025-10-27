package com.cmsr.onebase.module.flow.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程管理 - 分页查询流程 Request VO
 */
@Data
@Schema(description = "流程管理 - 分页查询流程请求参数")
public class PageExecutionLogReqVO extends PageParam {

    private static final long serialVersionUID = 1L;

    @Schema(description = "应用ID", example = "1")
    private Long applicationId;

    @Schema(description = "流程ID", example = "1")
    private Long processId;


}
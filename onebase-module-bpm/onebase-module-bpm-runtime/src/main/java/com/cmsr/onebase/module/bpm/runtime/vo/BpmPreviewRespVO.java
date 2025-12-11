package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 流程预览请求VO
 *
 * @author liyang
 * @date 2025-11-11
 */
@Schema(description = "流程预览VO")
@Data
public class BpmPreviewRespVO {
    @Schema(description = "流程实例ID")
    private Long instanceId;

    @Schema(description = "业务ID")
    private Long businessId;

    @Schema(description = "业务UUID")
    private String businessUuid;

    @Schema(description = "流程名称")
    private String flowName;

    @Schema(description = "流程版本")
    private String bpmVersion;

    @Schema(description = "流程定义JSON")
    private String bpmDefJson;
}

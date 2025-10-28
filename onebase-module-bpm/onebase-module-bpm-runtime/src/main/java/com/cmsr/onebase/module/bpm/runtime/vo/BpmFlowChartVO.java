package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BpmFlowChartVO {
    @Schema(description = "流程节点编码")
    private String nodeCode;

    @Schema(description = "流程节点名称")
    private String nodeName;

    @Schema(description = "审批人")
    private String approver;

    @Schema(description = "是否是当前节点")
    private String isCurrentNode;

}

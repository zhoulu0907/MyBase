package com.cmsr.onebase.module.bpm.runtime.vo;

import com.cmsr.onebase.module.bpm.core.vo.UserBasicInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 待办任务VO
 */
@Schema(description = "待办任务VO")
@Data
public class BpmFlowTodoTaskVO {
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程标题")
    private String processTitle;

    @Schema(description = "发起人")
    private UserBasicInfoVO initiator;

    @Schema(description = "当前节点状态")
    private String flowStatus;

    @Schema(description = "表单摘要")
    private String formSummary;

    @Schema(description = "到达时间")
    private LocalDateTime arrivalTime;

    @Schema(description = "发起时间")
    private LocalDateTime submitTime;

    @Schema(description = "任务id")
    private Long taskId;

    @Schema(description = "流程实例id")
    private Long instanceId;

    @Schema(description = "流程表单，实际对应pageSetId")
    private String businessId;

    @Schema(description = "流程节点编码")
    private String nodeCode;
}

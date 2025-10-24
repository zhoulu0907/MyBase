package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 已办任务VO
 */
@Schema(description = "待办任务VO")
@Data
public class BpmFlowDoneTaskVO {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "流程标题")
    private String processTitle;

    @Schema(description = "发起人")
    private String initiator;

    @Schema(description = "表单摘要")
    private String formSummary;

    @Schema(description = "处理时间")
    private LocalDateTime handleTime;

    @Schema(description = "处理操作")
    private String handleOperation;

}

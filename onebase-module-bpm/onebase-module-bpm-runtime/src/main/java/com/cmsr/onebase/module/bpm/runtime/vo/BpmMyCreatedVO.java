package com.cmsr.onebase.module.bpm.runtime.vo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "我创建的流程 - 响应VO")
@Data
public class BpmMyCreatedVO {

    @Schema(description = "流程实例ID")
    private Long id;

    @Schema(description = "流程标题")
    private String processTitle;

    @Schema(description = "流程状态编码")
    private String flowStatus;


    @Schema(description = "当前节点处理人")
    private String currentNodeHandler;

    @Schema(description = "发起时间")
    private LocalDateTime submitTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间（处理时间）")
    private LocalDateTime updateTime;

    @Schema(description = "任务id")
    private Long taskId;

    @Schema(description = "流程实例id")
    private Long instanceId;
}
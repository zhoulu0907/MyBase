package com.cmsr.onebase.module.bpm.runtime.vo.taskcenter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "我创建的流程 - 响应VO")
@Data
public class BpmMyCreatedVO {

    @Schema(description = "流程实例ID")
    private Long id;

    @Schema(description = "流程标题")
    private String processTitle;

    @Schema(description = "流程状态编码")
    private String flowStatus;

    @Schema(description = "表单摘要")
    private String formSummary;

    @Schema(description = "当前节点处理人")
    private List<Map<String, Object>> currentNodeHandler;

    @Schema(description = "发起时间")
    private LocalDateTime submitTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间（处理时间）")
    private LocalDateTime updateTime;

    @Schema(description = "流程实例id")
    private Long instanceId;

    @Schema(description = "任务id")
    private Long taskId;

    @Schema(description = "流程表单，对应menuUuid")
    private String businessUuid;

    @Schema(description = "页面集Id")
    private Long pageSetId;
}
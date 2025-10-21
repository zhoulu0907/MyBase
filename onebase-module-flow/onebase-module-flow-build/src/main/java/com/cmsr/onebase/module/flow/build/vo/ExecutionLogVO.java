package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 流程管理 - 流程视图对象
 */
@Data
@Schema(description = "流程管理 - 流程视图对象")
public class ExecutionLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "流程ID")
    private Long id;

    @Schema(description = "触发用户ID")
    private Long triggerUserId;

    @Schema(description = "执行UUID")
    private String executionUuid;

    @Schema(description = "流程ID")
    private Long processId;

    @Schema(description = "流程名称")
    private String processName;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行结果")
    private String executionResult;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "日志文本")
    private String logText;

}
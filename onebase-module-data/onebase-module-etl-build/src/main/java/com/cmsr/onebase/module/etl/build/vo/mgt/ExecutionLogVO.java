package com.cmsr.onebase.module.etl.build.vo.mgt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "数据工厂 - ETL - 执行日志实体")
@Data
public class ExecutionLogVO {

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "ETL ID")
    private Long workflowId;

    @Schema(description = "业务日期")
    private LocalDateTime businessDate;

    @Schema(description = "执行开始时间")
    private LocalDateTime startTime;

    @Schema(description = "执行结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行时长")
    private Long duration;

    @Schema(description = "触发类型")
    private String triggerType;

    @Schema(description = "触发用户")
    private String triggerUser;

    @Schema(description = "任务状态")
    private String taskStatus;
}

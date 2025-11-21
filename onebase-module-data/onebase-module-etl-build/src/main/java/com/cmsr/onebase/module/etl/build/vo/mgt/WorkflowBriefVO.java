package com.cmsr.onebase.module.etl.build.vo.mgt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Schema(description = "数据工厂 - 数据流 VO")
@Data
public class WorkflowBriefVO {

    @Schema(description = "数据流ID")
    private Long id;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "数据流名称")
    private String flowName;

    @Schema(description = "是否启用")
    private Integer enableStatus;

    @Schema(description = "更新策略")
    private String scheduleStrategy;

    @Schema(description = "最近一次的执行状态")
    private String isSyncDone;

    @Schema(description = "输入数据源")
    private List<String> sourceTables = Collections.emptyList();

    @Schema(description = "输出数据源")
    private String targetTable;

    @Schema(description = "最后数据更新时间")
    private LocalDateTime lastSuccessTime;

}

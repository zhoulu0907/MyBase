package com.cmsr.onebase.module.etl.build.service.etl.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "数据工厂 - 数据流 VO")
@Data
public class ETLWorkflowBriefVO {

    @Schema(description = "数据流ID")
    private Long id;

    @Schema(description = "数据流名称")
    private String name;

    @Schema(description = "更新策略")
    private String scheduleStrategy;

    @Schema(description = "执行状态")
    private String status;

    @Schema(description = "输入数据源")
    private List<String> inputTables;

    @Schema(description = "输出数据源")
    private String outputTable;

    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdateTime;

}

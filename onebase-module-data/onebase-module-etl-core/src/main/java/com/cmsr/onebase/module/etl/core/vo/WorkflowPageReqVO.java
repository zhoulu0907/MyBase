package com.cmsr.onebase.module.etl.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "数据工厂 - ETL - 分页查询ETL 请求VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkflowPageReqVO extends PageParam {

    @Schema(description = "应用ID")
    @NotNull
    private Long applicationId;

    @Schema(description = "数据流名称")
    private String flowName;

    @Schema(description = "数据流分类")
    private String scheduleStrategy;

    @Schema(description = "是否启用")
    private Integer enableStatus;
}

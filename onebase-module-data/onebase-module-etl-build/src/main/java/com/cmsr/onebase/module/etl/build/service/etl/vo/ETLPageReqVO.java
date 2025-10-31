package com.cmsr.onebase.module.etl.build.service.etl.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "ETL - 分页查询 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ETLPageReqVO extends PageParam {

    @Schema(description = "数据流名称")
    private String name;

    @Schema(description = "数据流分类")
    private String scheduleStrategy;

    @Schema(description = "是否启用")
    private String isEnabled;

    @Schema(description = "最近一次任务状态")
    private String jobStatus;
}

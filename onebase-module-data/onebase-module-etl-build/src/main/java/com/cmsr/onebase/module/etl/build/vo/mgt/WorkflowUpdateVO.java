package com.cmsr.onebase.module.etl.build.vo.mgt;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "数据工厂 - ETL - 更新ETL配置VO")
@Data
public class WorkflowUpdateVO extends WorkflowCreateVO {

    @Schema(description = "ETL ID")
    @NotNull
    private Long id;

}

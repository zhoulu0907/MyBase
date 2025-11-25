package com.cmsr.onebase.module.etl.build.vo.preview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "数据工厂 - 数据源管理 - 预览表请求实体")
@Data
public class TablePreviewVO {

    @Schema(description = "数据源ID")
    @NotNull
    private Long datasourceId;

    @Schema(description = "数据表ID")
    @NotNull
    private Long tableId;

}

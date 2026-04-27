package com.cmsr.onebase.module.etl.build.vo.preview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "数据工厂 - 数据源管理 - 预览表请求实体")
@Data
public class TablePreviewVO {

    @Schema(description = "数据源ID")
    @NotBlank(message = "数据源UUID不能为空")
    private String datasourceUuid;

    @Schema(description = "数据表ID")
    @NotBlank(message = "数据表UUID不可为空")
    private String tableUuid;

}

package com.cmsr.onebase.module.etl.build.service.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 支持的数据源类型")
@Data
public class SupportedDatasourceVO {

    @Schema(description = "数据源类型")
    private String datasourceType;

    @Schema(description = "显示名称")
    private String displayName;
}

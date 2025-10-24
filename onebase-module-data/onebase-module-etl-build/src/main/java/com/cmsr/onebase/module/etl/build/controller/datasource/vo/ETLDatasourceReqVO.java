package com.cmsr.onebase.module.etl.build.controller.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "数据工厂 - 数据源创建/修改 VO")
@Data
public class ETLDatasourceReqVO {

    @Schema(description = "数据源ID")
    private Long id;

    @Schema(description = "数据源编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "数据源编号不能为空")
    @Size(max = 40, message = "数据源编号不可超过40个字符")
    private String datasourceCode;

    @Schema(description = "数据源名称信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源名称不能为空")
    @Size(max = 200, message = "数据源名称不可超过200个字符")
    private String datasourceName;

    @Schema(description = "数据源类型信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源类型不能为空")
    private String datasourceType;

    @Schema(description = "数据源配置信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源配置信息不能为空")
    private String config;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long appId;

    @Schema(description = "只读", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean readonly;
}

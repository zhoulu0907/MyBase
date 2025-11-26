package com.cmsr.onebase.module.etl.build.vo.datasource;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "数据工厂 - 数据源 - 数据源更新VO")
@Data
public class ETLDatasourceUpdateReqVO {
    @Schema(description = "数据源ID")
    @NotNull(message = "数据源ID不可为空")
    private Long id;

    @Schema(description = "应用ID")
    @NotNull(message = "应用ID不可为空")
    private Long applicationId;

    @Schema(description = "数据源名称")
    @NotBlank(message = "数据源名称不能为空")
    @Size(min = 1, max = 200, message = "数据源名称最大长度为200字符")
    private String datasourceName;

    @Schema(description = "数据源描述")
    private String declaration;

    @Schema(description = "数据源类型信息")
    @NotBlank(message = "数据源类型不能为空")
    private String datasourceType;

    @Schema(description = "数据源配置信息")
    @NotBlank(message = "数据源配置信息不能为空")
    private JsonNode config;

    @Schema(description = "只读", requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "true")
    private Boolean readonly;
}

package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 数据源连接测试 Request VO")
@Data
public class DatasourceTestConnectionReqVO {

    @Schema(description = "数据源类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "POSTGRESQL")
    @NotBlank(message = "数据源类型不能为空")
    @Size(max = 64, message = "数据源类型长度不能超过64个字符")
    private String datasourceType;

    @Schema(description = "数据源配置信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数据源配置信息不能为空")
    private Map<String, Object> config;

}

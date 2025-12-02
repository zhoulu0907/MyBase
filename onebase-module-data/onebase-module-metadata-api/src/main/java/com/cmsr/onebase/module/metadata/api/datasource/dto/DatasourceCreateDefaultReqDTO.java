package com.cmsr.onebase.module.metadata.api.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 数据源创建默认请求 DTO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Schema(description = "数据源创建默认请求 DTO")
@Data
public class DatasourceCreateDefaultReqDTO {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @Schema(description = "应用唯一标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "my-app")
    @NotBlank(message = "应用唯一标识不能为空")
    private String appUid;
}
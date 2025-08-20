package com.cmsr.onebase.module.metadata.api.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * RPC 服务 - 创建默认数据源请求 DTO
 *
 * @author matianyu
 * @date 2025-08-19
 */
@Schema(description = "RPC 服务 - 创建默认数据源请求 DTO")
@Data
public class DatasourceCreateDefaultReqDTO {

    /**
     * 应用ID
     */
    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    /**
     * 应用唯一UID
     */
    @Schema(description = "应用唯一UID", requiredMode = Schema.RequiredMode.REQUIRED, example = "app_8f3b2c1a")
    @NotBlank(message = "应用UID不能为空")
    private String appUid;
}

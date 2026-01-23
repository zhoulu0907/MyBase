package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 创建连接器环境配置请求VO
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Data
@Schema(description = "创建连接器环境配置请求")
public class CreateFlowConnectorEnvReqVO {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "应用ID不能为空")
    private Long applicationId;

    @Schema(description = "环境名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "开发数据库")
    @NotBlank(message = "环境名称不能为空")
    @Size(max = 128, message = "环境名称不能超过128个字符")
    private String envName;

    @Schema(description = "环境编码（DEV/TEST/PROD等）", requiredMode = Schema.RequiredMode.REQUIRED, example = "DEV")
    @NotBlank(message = "环境编码不能为空")
    @Size(max = 64, message = "环境编码不能超过64个字符")
    private String envCode;

    @Schema(description = "连接器类型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "DATABASE_MYSQL")
    @NotBlank(message = "连接器类型编号不能为空")
    private String typeCode;

    @Schema(description = "环境URL", example = "jdbc:mysql://dev-db:3306")
    @Size(max = 512, message = "环境URL不能超过512个字符")
    private String envUrl;

    @Schema(description = "认证方式", example = "BASIC")
    private String authType;

    @Schema(description = "认证配置")
    private JsonNode authConfig;

    @Schema(description = "环境描述", example = "开发环境数据库配置")
    @Size(max = 512, message = "环境描述不能超过512个字符")
    private String description;

    @Schema(description = "扩展配置")
    private JsonNode extraConfig;

    @Schema(description = "排序序号", example = "0")
    private Integer sortOrder;
}

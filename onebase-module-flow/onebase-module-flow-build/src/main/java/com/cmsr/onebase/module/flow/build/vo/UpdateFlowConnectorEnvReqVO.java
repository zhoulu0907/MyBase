package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 更新连接器环境配置请求VO
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Data
@Schema(description = "更新连接器环境配置请求")
public class UpdateFlowConnectorEnvReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "环境名称", example = "开发数据库")
    @Size(max = 128, message = "环境名称不能超过128个字符")
    private String envName;

    @Schema(description = "环境编码", example = "DEV")
    @Size(max = 64, message = "环境编码不能超过64个字符")
    private String envCode;

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

    @Schema(description = "动作配置（JSON格式）")
    private JsonNode config;

    @Schema(description = "启用状态", example = "1")
    private Integer activeStatus;

    @Schema(description = "排序序号", example = "0")
    private Integer sortOrder;

    @Schema(description = "乐观锁版本号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "乐观锁版本号不能为空")
    private Long lockVersion;
}

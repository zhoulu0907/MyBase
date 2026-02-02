package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 连接器环境配置VO
 * <p>
 * 用于返回环境配置的完整信息，包含敏感配置
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Data
@Schema(description = "连接器环境配置")
public class FlowConnectorEnvVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "环境配置UUID")
    private String envUuid;

    @Schema(description = "环境名称")
    private String envName;

    @Schema(description = "环境编码")
    private String envCode;

    @Schema(description = "连接器类型编号")
    private String typeCode;

    @Schema(description = "环境URL")
    private String envUrl;

    @Schema(description = "认证方式")
    private String authType;

    @Schema(description = "认证配置")
    private JsonNode authConfig;

    @Schema(description = "环境描述")
    private String description;

    @Schema(description = "动作配置（JSON格式）")
    private JsonNode config;

    @Schema(description = "启用状态")
    private Integer activeStatus;

    @Schema(description = "排序序号")
    private Integer sortOrder;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

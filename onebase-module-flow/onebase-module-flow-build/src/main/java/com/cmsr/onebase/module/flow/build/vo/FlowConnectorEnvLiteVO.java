package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 连接器环境配置精简VO
 * <p>
 * 用于列表展示，不包含敏感配置信息（authConfig、extraConfig）
 *
 * @author zhoulu
 * @since 2026-01-23
 */
@Data
@Schema(description = "连接器环境配置（精简）")
public class FlowConnectorEnvLiteVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "环境配置编号")
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

    @Schema(description = "描述")
    private String description;

    @Schema(description = "启用状态")
    private Integer activeStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

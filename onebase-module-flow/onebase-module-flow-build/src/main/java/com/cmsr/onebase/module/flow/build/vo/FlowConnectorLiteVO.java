package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 连接器实例列表VO
 * <p>
 * 用于连接器实例列表页面展示，包含配置状态、环境信息等
 *
 * @author zhoulu
 * @since 2026-01-22
 */
@Schema(description = "连接器实例列表VO")
@Data
public class FlowConnectorLiteVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "连接器UUID")
    private String connectorUuid;

    @Schema(description = "连接器名称")
    private String connectorName;

    @Schema(description = "连接器类型")
    private String typeCode;

    @Schema(description = "连接器描述")
    private String description;

    @Schema(description = "环境名称")
    private String envName;

    @Schema(description = "环境编码")
    private String envCode;

    @Schema(description = "配置状态（configured-已配置, unconfigured-未配置）")
    private String configStatus;

    /**
     * 前端使用的配置状态字段
     * 映射到 configStatus，保持向后兼容
     */
    @Schema(description = "配置状态（configured-已配置, unconfigured-未配置）")
    private String status;

    @Schema(description = "启用状态（0-禁用，1-启用）")
    private Integer activeStatus;

    /**
     * 前端使用的环境信息字段
     * 格式："{envName} ({envCode})" 或仅 envName
     */
    @Schema(description = "环境信息（显示用）")
    private String environment;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

package com.cmsr.onebase.module.metadata.api.backupmanage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据源 DTO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "RPC 服务 - 数据源信息")
@Data
public class MetadataDatasourceDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "数据源UUID")
    private String datasourceUuid;

    @Schema(description = "数据源名称")
    private String datasourceName;

    @Schema(description = "数据源编码")
    private String code;

    @Schema(description = "数据源类型")
    private String datasourceType;

    @Schema(description = "数据源配置信息")
    private String config;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private Long creator;

    @Schema(description = "更新者")
    private Long updater;

    @Schema(description = "是否删除")
    private Long deleted;

    @Schema(description = "租户ID")
    private Long tenantId;

}

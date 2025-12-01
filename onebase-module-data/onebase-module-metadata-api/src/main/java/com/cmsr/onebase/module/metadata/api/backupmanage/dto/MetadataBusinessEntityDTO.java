package com.cmsr.onebase.module.metadata.api.backupmanage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务实体 DTO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "RPC 服务 - 业务实体信息")
@Data
public class MetadataBusinessEntityDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "实体名称")
    private String displayName;

    @Schema(description = "实体编码")
    private String code;

    @Schema(description = "实体类型")
    private Integer entityType;

    @Schema(description = "实体描述")
    private String description;

    @Schema(description = "数据源ID")
    private Long datasourceId;

    @Schema(description = "对应数据表名")
    private String tableName;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "前端显示配置json")
    private String displayConfig;

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

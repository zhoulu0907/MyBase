package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务实体导出DTO
 * 对应表: metadata_business_entity
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "业务实体导出DTO")
@Data
public class MetadataBusinessEntityExportDTO {

    /**
     * 实体UUID
     */
    @Schema(description = "实体UUID")
    private String entityUuid;

    /**
     * 实体名称
     */
    @Schema(description = "实体名称")
    private String displayName;

    /**
     * 实体编码
     */
    @Schema(description = "实体编码")
    private String code;

    /**
     * 实体类型(1:自建表 2:复用已有表 3中间表)
     */
    @Schema(description = "实体类型(1:自建表 2:复用已有表 3中间表)")
    private Integer entityType;

    /**
     * 实体描述
     */
    @Schema(description = "实体描述")
    private String description;

    /**
     * 数据源UUID
     */
    @Schema(description = "数据源UUID")
    private String datasourceUuid;

    /**
     * 表名后缀 - 原tableName去掉app_uid_前缀（前5位）
     */
    @Schema(description = "表名后缀（原tableName去掉app_uid_前缀）")
    private String tableNameSuffix;

    /**
     * 前端显示配置json
     */
    @Schema(description = "前端显示配置json")
    private String displayConfig;

    /**
     * 状态：0 关闭，1 开启
     */
    @Schema(description = "状态：0 关闭，1 开启")
    private Integer status;
}

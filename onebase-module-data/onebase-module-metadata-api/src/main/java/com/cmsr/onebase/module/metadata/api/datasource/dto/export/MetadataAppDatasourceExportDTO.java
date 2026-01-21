package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 应用与数据源关联导出DTO
 * 对应表: metadata_app_and_datasource
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "应用与数据源关联导出DTO")
@Data
public class MetadataAppDatasourceExportDTO {

    /**
     * 数据源UUID
     */
    @Schema(description = "数据源UUID")
    private String datasourceUuid;

    /**
     * 数据源类型 - 导出时设为null，导入时从参数获取
     */
    @Schema(description = "数据源类型")
    private String datasourceType;

    // appUid 导出时设为null，导入时从参数获取
}

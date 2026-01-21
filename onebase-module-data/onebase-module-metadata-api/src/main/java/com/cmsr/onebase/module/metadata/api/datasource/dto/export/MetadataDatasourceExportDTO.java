package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据源导出DTO
 * 对应表: metadata_datasource
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "数据源导出DTO")
@Data
public class MetadataDatasourceExportDTO {

    /**
     * 数据源UUID - 保持不变
     */
    @Schema(description = "数据源UUID")
    private String datasourceUuid;

    /**
     * 数据源来源：0系统默认，1自有，2外部
     */
    @Schema(description = "数据源来源：0系统默认，1自有，2外部")
    private Integer datasourceOrigin;

    // 以下字段导出时设为null，导入时从参数获取或使用默认值
    // datasourceName, code, datasourceType, config, description
}

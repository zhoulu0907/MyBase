package com.cmsr.onebase.module.metadata.api.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据源保存请求 DTO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Schema(description = "数据源导入请求 DTO")
@Data
public class DatasourceImportReqDTO {

    @Schema(description = "数据源名称", example = "默认数据源")
    private String name;

    @Schema(description = "数据源类型", example = "mysql")
    private String datasourceType;

    @Schema(description = "数据源配置", example = "{}")
    private String config;

    @Schema(description = "备注", example = "默认数据源")
    private String remark;
}

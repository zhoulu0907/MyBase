package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 数据源类型 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 数据源类型 Response VO")
@Data
public class DatasourceTypeRespVO {

    @Schema(description = "数据源类型编码", example = "POSTGRESQL")
    private String datasourceType;

    @Schema(description = "显示名称", example = "PostgreSQL数据库")
    private String displayName;

    @Schema(description = "描述信息", example = "支持PostgreSQL 9.6及以上版本")
    private String description;

    @Schema(description = "默认端口", example = "5432")
    private Integer defaultPort;

    @Schema(description = "JDBC驱动类", example = "org.postgresql.Driver")
    private String jdbcDriverClass;

    @Schema(description = "URL模板", example = "jdbc:postgresql://{host}:{port}/{database}")
    private String urlTemplate;

    @Schema(description = "支持的功能特性", example = "[\"READ\", \"WRITE\", \"SCHEMA_DISCOVERY\"]")
    private List<String> supportFeatures;

}

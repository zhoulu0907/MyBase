package com.cmsr.onebase.module.metadata.api.datasource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * RPC 服务 - 数据源响应 DTO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "RPC 服务 - 数据源响应 DTO")
@Data
public class DatasourceRespDTO {

    @Schema(description = "数据源编号", example = "1024")
    private Long id;

    @Schema(description = "数据源名称", example = "用户数据库")
    private String datasourceName;

    @Schema(description = "数据源编码", example = "user_db")
    private String code;

    @Schema(description = "数据源类型", example = "POSTGRESQL")
    private String datasourceType;

    @Schema(description = "数据源配置信息")
    private Map<String, Object> config;

    @Schema(description = "描述", example = "用户相关数据的数据源")
    private String description;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1")
    private Long applicationId;
    
    @Schema(description = "数据源来源，0：系统默认，1：自有数据源，2：外部数据源", example = "1")
    private Integer datasourceOrigin;

}

package com.cmsr.onebase.module.metadata.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 数据源 Response VO")
@Data
public class DatasourceRespVO {

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

    @Schema(description = "运行模式", example = "0")
    private Integer runMode;

    @Schema(description = "应用ID", example = "1")
    private Long appId;
    
    @Schema(description = "数据源来源，0：系统默认，1：自有数据源，2：外部数据源", example = "1")
    private Integer datasourceOrigin;

}

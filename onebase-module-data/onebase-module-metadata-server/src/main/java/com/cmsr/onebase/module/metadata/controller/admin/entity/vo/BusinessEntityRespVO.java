package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 业务实体 Response VO")
@Data
public class BusinessEntityRespVO {

    @Schema(description = "实体编号", example = "1024")
    private Long id;

    @Schema(description = "实体名称", example = "用户实体")
    private String displayName;

    @Schema(description = "实体编码", example = "user_entity")
    private String code;

    @Schema(description = "实体类型", example = "1")
    private Integer entityType;

    @Schema(description = "实体描述", example = "用户相关的业务实体")
    private String description;

    @Schema(description = "数据源ID", example = "1")
    private Long datasourceId;

    @Schema(description = "对应数据表名", example = "sys_user")
    private String tableName;

    @Schema(description = "运行模式", example = "0")
    private Integer runMode;

    @Schema(description = "应用ID", example = "1")
    private Long appId;

    @Schema(description = "前端显示配置json", example = "{\"showFields\":[\"name\",\"code\"]}")
    private String displayConfig;

}

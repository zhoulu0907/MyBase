package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 业务实体 Response VO")
@Data
public class BusinessEntityRespVO {

    @Schema(description = "实体编号", example = "1024")
    private String id;

    @Schema(description = "实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String entityUuid;

    @Schema(description = "实体名称", example = "用户实体")
    private String displayName;

    @Schema(description = "实体编码", example = "user_entity")
    private String code;

    @Schema(description = "实体类型(1:自建表，2:复用已有表，3:中间表)", example = "1")
    private Integer entityType;

    @Schema(description = "实体描述", example = "用户相关的业务实体")
    private String description;

    @Schema(description = "数据源UUID", example = "01onal1s-0000-0000-0000-000000000001")
    private String datasourceUuid;

    @Schema(description = "对应数据表名", example = "sys_user")
    private String tableName;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "前端显示配置json", example = "{\"showFields\":[\"name\",\"code\"]}")
    private String displayConfig;

    @Schema(description = "关系类型", example = "主表:PARENT，子表:CHILD")
    private String relationType;

    @Schema(description = "状态：0 关闭，1 开启", example = "1")
    private Integer status;

}

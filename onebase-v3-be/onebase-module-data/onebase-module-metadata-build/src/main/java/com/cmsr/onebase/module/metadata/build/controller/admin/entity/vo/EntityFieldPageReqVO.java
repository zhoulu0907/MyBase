package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 实体字段分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EntityFieldPageReqVO extends PageParam {

    @Schema(description = "实体ID", example = "1")
    private String entityId;

    @Schema(description = "字段名称", example = "user_name")
    private String fieldName;

    @Schema(description = "显示名称", example = "用户名")
    private String displayName;

    @Schema(description = "字段类型", example = "VARCHAR")
    private String fieldType;

    @Schema(description = "是否系统字段", example = "false")
    private Boolean isSystemField;

    @Schema(description = "是否主键", example = "false")
    private Boolean isPrimaryKey;

    @Schema(description = "是否必填", example = "true")
    private Boolean isRequired;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "字段编码", example = "USER_NAME")
    private String fieldCode;

}

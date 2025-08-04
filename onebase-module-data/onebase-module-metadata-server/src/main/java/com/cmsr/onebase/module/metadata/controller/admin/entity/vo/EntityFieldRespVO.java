package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 实体字段 Response VO")
@Data
public class EntityFieldRespVO {

    @Schema(description = "字段编号", example = "1024")
    private Long id;

    @Schema(description = "实体ID", example = "1")
    private Long entityId;

    @Schema(description = "字段名称", example = "user_name")
    private String fieldName;

    @Schema(description = "显示名称", example = "用户名")
    private String displayName;

    @Schema(description = "字段类型", example = "VARCHAR")
    private String fieldType;

    @Schema(description = "数据长度", example = "255")
    private Integer dataLength;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "默认值", example = "admin")
    private String defaultValue;

    @Schema(description = "字段描述", example = "用户登录名")
    private String description;

    @Schema(description = "是否系统字段", example = "false")
    private Boolean isSystemField;

    @Schema(description = "是否主键", example = "false")
    private Boolean isPrimaryKey;

    @Schema(description = "是否必填", example = "true")
    private Boolean isRequired;

    @Schema(description = "是否唯一", example = "true")
    private Boolean isUnique;

    @Schema(description = "是否允许空值", example = "false")
    private Boolean allowNull;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "校验规则配置", example = "1")
    private Long validationRulesId;

    @Schema(description = "运行模式", example = "0")
    private Integer runMode;

    @Schema(description = "应用ID", example = "1")
    private Long appId;
    
    @Schema(description = "字段状态，0：开启，1：关闭", example = "0")
    private Integer status;

}

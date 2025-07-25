package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 实体字段创建/修改 Request VO")
@Data
public class EntityFieldSaveReqVO {

    @Schema(description = "字段编号", example = "1024")
    private Long id;

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实体ID不能为空")
    private Long entityId;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "user_name")
    @NotBlank(message = "字段名称不能为空")
    @Size(max = 128, message = "字段名称长度不能超过128个字符")
    private String fieldName;

    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户名")
    @NotBlank(message = "显示名称不能为空")
    @Size(max = 128, message = "显示名称长度不能超过128个字符")
    private String displayName;

    @Schema(description = "字段类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "VARCHAR")
    @NotBlank(message = "字段类型不能为空")
    @Size(max = 64, message = "字段类型长度不能超过64个字符")
    private String fieldType;

    @Schema(description = "数据长度", example = "255")
    private Integer dataLength;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "默认值", example = "admin")
    private String defaultValue;

    @Schema(description = "字段描述", example = "用户登录名")
    @Size(max = 256, message = "字段描述长度不能超过256个字符")
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

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "应用ID不能为空")
    private Long appId;

    @Schema(description = "版本锁标识", example = "0")
    private Integer lockVersion;

}

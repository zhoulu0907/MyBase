package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理后台 - 字段创建项 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 字段创建项 VO")
@Data
public class EntityFieldCreateItemVO {

    @Schema(description = "字段名", requiredMode = Schema.RequiredMode.REQUIRED, example = "username")
    @NotBlank(message = "字段名不能为空")
    @Size(max = 40, message = "字段名长度不能超过40个字符")
    @Pattern(regexp = "^[a-zA-Z_][a-zA-Z0-9_]*$", message = "字段名只能包含英文字母、数字和下划线，且必须以字母或下划线开头")
    private String fieldName;

    @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户名")
    @NotBlank(message = "显示名称不能为空")
    @Size(max = 50, message = "显示名称长度不能超过50个字符")
    private String displayName;

    @Schema(description = "字段类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "VARCHAR")
    @NotBlank(message = "字段类型不能为空")
    private String fieldType;

    @Schema(description = "描述", example = "系统登录用户名")
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;

    @Schema(description = "是否必填：0-是，1-不是", example = "0")
    private Integer isRequired;

    @Schema(description = "是否唯一：0-是，1-不是", example = "0")
    private Integer isUnique;

    @Schema(description = "默认值", example = "")
    private String defaultValue;

    @Schema(description = "排序顺序", example = "10")
    private Integer sortOrder;

}

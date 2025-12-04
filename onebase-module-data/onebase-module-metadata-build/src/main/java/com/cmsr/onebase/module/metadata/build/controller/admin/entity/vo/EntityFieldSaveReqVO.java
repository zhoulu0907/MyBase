package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 实体字段保存 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 实体字段保存 Request VO")
@Data
public class EntityFieldSaveReqVO {

    @Schema(description = "字段ID", example = "3001")
    private String id;

    @Schema(description = "字段UUID（更新时可用于定位记录）", example = "01onal1s-0000-0000-0000-000000000003")
    private String fieldUuid;

    @Schema(description = "实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String entityUuid;

    @Schema(description = "实体ID（兼容旧版，与entityUuid二选一）", example = "164329365983232001")
    private String entityId;

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

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "默认值", example = "")
    private String defaultValue;

    @Schema(description = "描述", example = "系统登录用户名")
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;

    @Schema(description = "是否必填：0-是，1-不是", example = "0")
    private Integer isRequired;

    @Schema(description = "是否唯一：0-是，1-不是", example = "0")
    private Integer isUnique;

    @Schema(description = "排序顺序", example = "10")
    private Integer sortOrder;

    @Schema(description = "应用ID", example = "12345")
    private String applicationId;


    @Schema(description = "字段选项列表（当字段为单/多选时可传入，若提供则整体替换）")
    private List<FieldOptionRespVO> options;

    @Schema(description = "字段约束配置（长度/正则，若提供则整体替换）")
    private FieldConstraintRespVO constraints;

    @Schema(description = "自动编号配置（若提供则整体替换）")
    private AutoNumberConfigReqVO autoNumber;

    @Schema(description = "关联的字典类型ID,用于SELECT/MULTI_SELECT字段复用系统字典,为null时使用自定义选项", example = "1001")
    private Long dictTypeId;

}

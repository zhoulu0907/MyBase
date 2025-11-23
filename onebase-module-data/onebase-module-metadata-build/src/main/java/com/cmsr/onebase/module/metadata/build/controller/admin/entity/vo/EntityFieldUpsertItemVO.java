package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

/**
 * 管理后台 - 字段批量保存（增/改/删）项 VO
 */
@Schema(description = "管理后台 - 字段批量保存（增/改/删）项 VO")
@Data
public class EntityFieldUpsertItemVO {

    @Schema(description = "字段ID，存在则更新；不存在则新增；当 isDeleted=true 时必须存在", example = "3001")
    private String id;

    @Schema(description = "是否删除。true 表示删除该字段（需提供 id）", example = "false")
    private Boolean isDeleted;

    // 以下字段用于新增或更新（更新时可部分提供，未提供的将沿用原值）
    @Schema(description = "字段名", example = "username")
    @Size(max = 40, message = "字段名长度不能超过40个字符")
    @Pattern(regexp = "^[a-zA-Z_][a-zA-Z0-9_]*$", message = "字段名只能包含英文字母、数字和下划线，且必须以字母或下划线开头")
    private String fieldName;

    @Schema(description = "显示名称", example = "用户名")
    @Size(max = 50, message = "显示名称长度不能超过50个字符")
    private String displayName;

    @Schema(description = "字段类型", example = "VARCHAR")
    private String fieldType;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "默认值", example = "")
    private String defaultValue;

    @Schema(description = "描述", example = "系统登录用户名")
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;

    @Schema(description = "是否必填：1-是，0-否", example = "1")
    private Integer isRequired;

    @Schema(description = "是否唯一：1-是，0-否", example = "1")
    private Integer isUnique;

    @Schema(description = "排序顺序", example = "10")
    private Integer sortOrder;

/*     @Schema(description = "字段编码", example = "USER_NAME")
    @Size(max = 60, message = "字段编码长度不能超过60个字符")
    private String fieldCode; */

    @Schema(description = "是否系统字段", example = "0")
    private Integer isSystemField;

    @Schema(description = "字段选项列表（当字段为单/多选时可传入，若提供则整体替换）")
    private List<FieldOptionRespVO> options;

    @Schema(description = "字段约束配置（长度/正则，若提供则整体替换）")
    private FieldConstraintRespVO constraints;

    @Schema(description = "自动编号配置（若提供则整体替换）")
    private AutoNumberConfigReqVO autoNumber;

    @Schema(description = "关联的字典类型ID,用于SELECT/MULTI_SELECT字段复用系统字典,为null时使用自定义选项", example = "1001")
    private Long dictTypeId;

    @Schema(description = "数据选择配置，目标表的配置; 数据单选时使用")
    private DataSelectionConfig dataSelectionConfig;
}

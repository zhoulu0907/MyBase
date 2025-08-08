package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

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
    private String fieldName;

    @Schema(description = "显示名称", example = "用户名")
    @Size(max = 50, message = "显示名称长度不能超过50个字符")
    private String displayName;

    @Schema(description = "字段类型", example = "VARCHAR")
    private String fieldType;

    @Schema(description = "数据长度", example = "50")
    private Integer dataLength;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "默认值", example = "")
    private String defaultValue;

    @Schema(description = "描述", example = "系统登录用户名")
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;

    @Schema(description = "是否必填", example = "true")
    private Boolean isRequired;

    @Schema(description = "是否唯一", example = "true")
    private Boolean isUnique;

    @Schema(description = "允许空值", example = "false")
    private Boolean allowNull;

    @Schema(description = "排序顺序", example = "10")
    private Integer sortOrder;

    @Schema(description = "字段编码", example = "USER_NAME")
    @Size(max = 60, message = "字段编码长度不能超过60个字符")
    private String fieldCode;
}

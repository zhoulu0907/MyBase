package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * ER图字段VO
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Schema(description = "管理后台 - ER图字段")
@Data
public class ERFieldVO {

    @Schema(description = "字段ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long fieldId;

    @Schema(description = "字段UUID", example = "019b021a-c7f2-79a7-ad2e-07984e02e575")
    private String fieldUuid;

    @Schema(description = "字段名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "user_name")
    private String fieldName;

    @Schema(description = "字段显示名", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户名")
    private String displayName;

    @Schema(description = "字段类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "VARCHAR")
    private String fieldType;

    @Schema(description = "数据长度", example = "255")
    private Integer dataLength;

    @Schema(description = "字段描述", example = "用户登录名")
    private String description;

    @Schema(description = "是否必填：0-是，1-不是", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer isRequired;

    @Schema(description = "是否唯一：0-是，1-不是", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer isUnique;

    @Schema(description = "是否主键：0-是，1-不是", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer isPrimaryKey;

    @Schema(description = "是否系统字段：0-是，1-不是", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer isSystemField;

    @Schema(description = "默认值", example = "admin")
    private String defaultValue;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "关联的字典类型ID", example = "1024")
    private Long dictTypeId;

    @Schema(description = "字段选项列表（单/多选字段专用）")
    private List<FieldOptionRespVO> options;
}

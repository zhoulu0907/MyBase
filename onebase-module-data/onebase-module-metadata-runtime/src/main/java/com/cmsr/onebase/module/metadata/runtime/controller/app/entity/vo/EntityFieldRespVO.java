package com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 运行态 - 实体字段 Response VO
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Schema(description = "运行态 - 实体字段 Response VO")
@Data
public class EntityFieldRespVO {

    @Schema(description = "字段编号", example = "1024")
    private String id;

    @Schema(description = "字段UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String fieldUuid;

    @Schema(description = "实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String entityUuid;

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

    @Schema(description = "是否系统字段：0-是，1-不是", example = "1")
    private Integer isSystemField;

    @Schema(description = "是否主键：0-是，1-不是", example = "1")
    private Integer isPrimaryKey;

    @Schema(description = "是否必填：0-是，1-不是", example = "0")
    private Integer isRequired;

    @Schema(description = "是否唯一：0-是，1-不是", example = "0")
    private Integer isUnique;

    @Schema(description = "排序", example = "1")
    private Integer sortOrder;

    @Schema(description = "校验规则配置", example = "1")
    private String validationRulesId;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1001")
    private Long applicationId;

    @Schema(description = "字段状态，0：开启，1：关闭", example = "0")
    private Integer status;

    @Schema(description = "字段编码", example = "USER_NAME")
    private String fieldCode;

    @Schema(description = "字段选项列表（单/多选字段专用）")
    private List<FieldOptionRespVO> options;

    @Schema(description = "字段约束配置（长度/正则）")
    private FieldConstraintRespVO constraints;

    @Schema(description = "自动编号完整配置（含规则项）")
    private AutoNumberConfigRespVO autoNumberConfig;

    @Schema(description = "关联的字典类型ID", example = "1001")
    private Long dictTypeId;

    @Schema(description = "数据选择配置")
    private DataSelectionConfig dataSelectionConfig;
}

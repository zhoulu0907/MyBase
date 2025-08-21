package com.cmsr.onebase.module.metadata.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实体字段响应DTO
 *
 * @author matianyu
 * @date 2025-08-14
 */
@Schema(description = "RPC 服务 - 实体字段响应 DTO")
@Data
public class EntityFieldRespDTO {

    @Schema(description = "字段编号", example = "1024")
    private String id;

    @Schema(description = "实体ID", example = "1")
    private String entityId;

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

    @Schema(description = "是否系统字段：1-是，0-不是", example = "1")
    private Integer isSystemField;

    @Schema(description = "是否主键：1-是，0-不是", example = "1")
    private Integer isPrimaryKey;

    @Schema(description = "是否必填：1-是，0-不是", example = "0")
    private Integer isRequired;

    @Schema(description = "是否唯一：1-是，0-不是", example = "0")
    private Integer isUnique;

    @Schema(description = "是否允许空值：1-是，0-不是", example = "1")
    private Integer allowNull;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "字段配置", example = "{}")
    private String fieldConfig;

    @Schema(description = "扩展配置", example = "{}")
    private String extConfig;

    @Schema(description = "字段编码", example = "USER_NAME")
    private String fieldCode;

}

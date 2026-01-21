package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实体字段导出DTO
 * 对应表: metadata_entity_field
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "实体字段导出DTO")
@Data
public class MetadataEntityFieldExportDTO {

    /**
     * 字段UUID
     */
    @Schema(description = "字段UUID")
    private String fieldUuid;

    /**
     * 实体UUID
     */
    @Schema(description = "实体UUID")
    private String entityUuid;

    /**
     * 字段名称
     */
    @Schema(description = "字段名称")
    private String fieldName;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称")
    private String displayName;

    /**
     * 字段类型
     */
    @Schema(description = "字段类型")
    private String fieldType;

    /**
     * 数据长度
     */
    @Schema(description = "数据长度")
    private Integer dataLength;

    /**
     * 小数位数
     */
    @Schema(description = "小数位数")
    private Integer decimalPlaces;

    /**
     * 默认值
     */
    @Schema(description = "默认值")
    private String defaultValue;

    /**
     * 字段描述
     */
    @Schema(description = "字段描述")
    private String description;

    /**
     * 是否系统字段：1-是，0-不是
     */
    @Schema(description = "是否系统字段：1-是，0-不是")
    private Integer isSystemField;

    /**
     * 是否主键：1-是，0-不是
     */
    @Schema(description = "是否主键：1-是，0-不是")
    private Integer isPrimaryKey;

    /**
     * 是否必填：1-是，0-不是
     */
    @Schema(description = "是否必填：1-是，0-不是")
    private Integer isRequired;

    /**
     * 是否唯一：1-是，0-不是
     */
    @Schema(description = "是否唯一：1-是，0-不是")
    private Integer isUnique;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;

    /**
     * 校验规则配置
     */
    @Schema(description = "校验规则配置")
    private String validationRules;

    /**
     * 字段状态：1-开启，0-关闭
     */
    @Schema(description = "字段状态：1-开启，0-关闭")
    private Integer status;

    /**
     * 字段编码
     */
    @Schema(description = "字段编码")
    private String fieldCode;

    /**
     * 关联的字典类型ID
     */
    @Schema(description = "关联的字典类型ID")
    private Long dictTypeId;
}

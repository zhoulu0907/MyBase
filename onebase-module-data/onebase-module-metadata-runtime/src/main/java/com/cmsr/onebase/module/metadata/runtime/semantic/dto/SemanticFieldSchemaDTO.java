package com.cmsr.onebase.module.metadata.runtime.semantic.dto;
import com.cmsr.onebase.module.metadata.runtime.semantic.dto.enums.SemanticFieldTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "字段模型 DTO")
@Data
/**
 * 字段模型 DTO
 *
 * <p>描述实体字段的基础属性与约束信息。</p>
 */
public class SemanticFieldSchemaDTO {
    @Schema(description = "字段ID")
    private Long id;

    @Schema(description = "字段编码")
    private String fieldCode;

    @Schema(description = "字段名称")
    private String fieldName;

    @Schema(description = "字段 UUID")
    private String fieldUuid;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "字段类型")
    private String fieldType;

    @Schema(description = "字段类型枚举")
    private SemanticFieldTypeEnum fieldTypeEnum;

    @Schema(description = "数据长度")
    private Integer dataLength;

    @Schema(description = "小数位数")
    private Integer decimalPlaces;

    @Schema(description = "是否必填")
    private Boolean isRequired;

    @Schema(description = "是否唯一")
    private Boolean isUnique;

    @Schema(description = "是否系统字段")
    private Boolean isSystemField;

    @Schema(description = "是否主键")
    private Boolean isPrimaryKey;

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
        this.fieldTypeEnum = SemanticFieldTypeEnum.ofCode(fieldType);
    }

    private void setFieldTypeEnum(SemanticFieldTypeEnum fieldTypeEnum) {
        this.fieldTypeEnum = fieldTypeEnum;
    }
}

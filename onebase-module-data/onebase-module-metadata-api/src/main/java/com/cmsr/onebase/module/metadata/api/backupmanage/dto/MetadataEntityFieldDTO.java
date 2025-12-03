package com.cmsr.onebase.module.metadata.api.backupmanage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体字段 DTO
 *
 * @author matianyu
 * @date 2025-08-12
 */
@Schema(description = "RPC 服务 - 实体字段信息")
@Data
public class MetadataEntityFieldDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "字段UUID")
    private String fieldUuid;

    @Schema(description = "实体UUID")
    private String entityUuid;

    @Schema(description = "字段名称")
    private String fieldName;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "字段类型")
    private String fieldType;

    @Schema(description = "数据长度")
    private Integer dataLength;

    @Schema(description = "小数位数")
    private Integer decimalPlaces;

    @Schema(description = "默认值")
    private String defaultValue;

    @Schema(description = "字段描述")
    private String description;

    @Schema(description = "是否系统字段")
    private Boolean isSystemField;

    @Schema(description = "是否主键")
    private Boolean isPrimaryKey;

    @Schema(description = "是否必填")
    private Boolean isRequired;

    @Schema(description = "是否唯一")
    private Boolean isUnique;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "校验规则配置")
    private String validationRules;

    @Schema(description = "版本标识")
    private Long versionTag;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "字段状态")
    private Integer status;

    @Schema(description = "字段编码")
    private String fieldCode;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private Long creator;

    @Schema(description = "更新者")
    private Long updater;

    @Schema(description = "是否删除")
    private Long deleted;

    @Schema(description = "租户ID")
    private Long tenantId;

}

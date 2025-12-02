package com.cmsr.onebase.module.metadata.api.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实体字段响应 DTO
 *
 * @author matianyu
 * @date 2025-09-10
 */
@Schema(description = "实体字段响应 DTO")
@Data
public class EntityFieldRespDTO {

    @Schema(description = "字段ID", example = "1024")
    private Long id;

    @Schema(description = "实体ID", example = "1")
    private Long entityId;

    @Schema(description = "实体名称", example = "用户")
    private String entityDisplayName;

    @Schema(description = "实体表名", example = " user_info")
    private String tableName;

    @Schema(description = "字段名称", example = "username")
    private String fieldName;

    @Schema(description = "显示名称", example = "用户名")
    private String displayName;

    @Schema(description = "字段类型", example = "STRING")
    private String fieldType;

    @Schema(description = "小数位数", example = "2")
    private Integer decimalPlaces;

    @Schema(description = "默认值", example = "N/A")
    private String defaultValue;

    @Schema(description = "字段描述", example = "用户登录名")
    private String description;

    @Schema(description = "是否必填", example = "1")
    private Integer isRequired;

    @Schema(description = "是否唯一", example = "1")
    private Integer isUnique;

    @Schema(description = "是否系统字段", example = "0")
    private Integer isSystemField;

    @Schema(description = "是否主键", example = "0")
    private Integer isPrimaryKey;

    @Schema(description = "排序顺序", example = "1")
    private Integer sortOrder;

    @Schema(description = "字段编码", example = "USER_NAME")
    private String fieldCode;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用UUID", example = "app-uuid-xxx")
    private String applicationUuid;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

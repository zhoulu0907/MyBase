package com.cmsr.onebase.module.metadata.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理后台 - 字段详情 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 字段详情 Response VO")
@Data
public class EntityFieldDetailRespVO {

    @Schema(description = "字段ID", example = "3001")
    private String id;

    @Schema(description = "实体ID", example = "2001")
    private String entityId;

    @Schema(description = "实体名称", example = "用户信息")
    private String entityName;

    @Schema(description = "字段名", example = "username")
    private String fieldName;

    @Schema(description = "显示名称", example = "用户名")
    private String displayName;

    @Schema(description = "字段类型", example = "VARCHAR")
    private String fieldType;

    @Schema(description = "数据长度", example = "50")
    private Integer dataLength;

    @Schema(description = "小数位数", example = "null")
    private Integer decimalPlaces;

    @Schema(description = "默认值", example = "")
    private String defaultValue;

    @Schema(description = "描述", example = "系统登录用户名")
    private String description;

    @Schema(description = "是否必填", example = "true")
    private Boolean isRequired;

    @Schema(description = "是否唯一", example = "true")
    private Boolean isUnique;

    @Schema(description = "允许空值", example = "false")
    private Boolean allowNull;

    @Schema(description = "是否系统字段", example = "false")
    private Boolean isSystemField;

    @Schema(description = "是否主键", example = "false")
    private Boolean isPrimaryKey;

    @Schema(description = "排序顺序", example = "10")
    private Integer sortOrder;

    @Schema(description = "校验规则列表")
    private List<ValidationRuleItemVO> validationRules;

    @Schema(description = "创建时间", example = "2025-07-28T10:30:00")
    private LocalDateTime createTime;

    @Schema(description = "字段编码", example = "USER_NAME")
    private String fieldCode;

} 
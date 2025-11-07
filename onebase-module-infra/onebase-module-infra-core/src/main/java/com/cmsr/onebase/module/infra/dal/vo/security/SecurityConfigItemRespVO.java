package com.cmsr.onebase.module.infra.dal.vo.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 安全配置项响应VO
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Schema(description = "管理后台 - 安全配置项 Response VO")
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
public class SecurityConfigItemRespVO {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "配置键", example = "minLength")
    private String configKey;

    @Schema(description = "配置名称", example = "密码最小长度")
    private String configName;

    @Schema(description = "数据类型", example = "INTEGER")
    private String dataType;

    @Schema(description = "配置值", example = "8")
    private String configValue;

    @Schema(description = "描述", example = "密码最小长度")
    private String description;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "全量选项", example = "描述1,描述2")
    private String options;

    @Schema(description = "最大值", example = "10000")
    private Long maxValue;

    @Schema(description = "最小值", example = "1")
    private Long minValue;

    @Schema(description = "必填", example = "true")
    private String required;

    @Schema(description = "界面组件类型", example = "CHECKBOX ")
    private String widgetType;

}
package com.cmsr.onebase.module.infra.dal.vo.security;

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

    @Schema(description = "默认值", example = "8")
    private String defaultValue;

    @Schema(description = "描述", example = "密码最小长度")
    private String description;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

}

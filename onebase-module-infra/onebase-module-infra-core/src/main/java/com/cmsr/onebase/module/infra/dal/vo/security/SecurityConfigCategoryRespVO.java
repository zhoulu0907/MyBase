package com.cmsr.onebase.module.infra.dal.vo.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 安全配置分类响应VO
 *
 * @author chengyuansen
 * @date 2025-11-04
 */
@Schema(description = "管理后台 - 安全配置分类 Response VO")
@Data
public class SecurityConfigCategoryRespVO {

    @Schema(description = "分类ID", example = "1")
    private Long id;

    @Schema(description = "分类编码", example = "PASSWORD_POLICY")
    private String categoryCode;

    @Schema(description = "分类名称", example = "密码策略配置")
    private String categoryName;

    @Schema(description = "分类描述", example = "密码有效期、历史密码限制等策略")
    private String description;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

}

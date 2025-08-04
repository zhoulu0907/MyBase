package com.cmsr.onebase.module.metadata.controller.admin.validation.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台 - 校验规则分页 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验规则分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationRulePageReqVO extends PageParam {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12345")
    private Long appId;

    @Schema(description = "实体ID", example = "2001")
    private Long entityId;

    @Schema(description = "字段ID", example = "3001")
    private Long fieldId;

    @Schema(description = "校验类型", example = "FORMAT_VALIDATION")
    private String validationType;

    @Schema(description = "搜索关键词", example = "用户名")
    private String keyword;

} 
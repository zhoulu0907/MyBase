package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 长度校验响应VO
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Schema(description = "管理后台 - 长度校验响应 VO")
@Data
public class ValidationLengthRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "规则组名称", example = "用户信息校验")
    private String rgName;

    @Schema(description = "字段ID", example = "1")
    private Long fieldId;

    @Schema(description = "实体ID", example = "1")
    private Long entityId;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "规则组ID", example = "1")
    private Long groupId;

    @Schema(description = "是否启用", example = "1")
    private Integer isEnabled;

    @Schema(description = "最小长度", example = "1")
    private Integer minLength;

    @Schema(description = "最大长度", example = "100")
    private Integer maxLength;

    @Schema(description = "校验前是否去除前后空格", example = "1")
    private Integer trimBefore;

    @Schema(description = "提示信息", example = "字符长度必须在1-100之间")
    private String promptMessage;

    @Schema(description = "版本标识", example = "1")
    private Long versionTag;
}

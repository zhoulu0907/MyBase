package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 唯一性校验响应VO
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Schema(description = "管理后台 - 唯一性校验响应 VO")
@Data
public class ValidationUniqueRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "唯一性校验UUID", example = "01onal1s-0000-0000-0000-000000000008")
    private String uniqueUuid;

    @Schema(description = "规则组名称", example = "用户信息校验")
    private String rgName;

    @Schema(description = "字段UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String fieldUuid;

    @Schema(description = "实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String entityUuid;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "规则组UUID", example = "01onal1s-0000-0000-0000-000000000006")
    private String groupUuid;

    @Schema(description = "是否启用", example = "1")
    private Integer isEnabled;

    @Schema(description = "唯一性范围", example = "1")
    private Integer uniqueScope;

    @Schema(description = "提示信息", example = "该值已存在，请输入其他值")
    private String promptMessage;

    @Schema(description = "版本标识", example = "1")
    private Long versionTag;
}

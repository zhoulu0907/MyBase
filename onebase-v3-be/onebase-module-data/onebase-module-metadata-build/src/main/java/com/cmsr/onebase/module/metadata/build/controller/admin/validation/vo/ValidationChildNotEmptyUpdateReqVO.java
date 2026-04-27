package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 子表非空校验更新请求VO
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Schema(description = "管理后台 - 子表非空校验更新请求 VO")
@Data
public class ValidationChildNotEmptyUpdateReqVO {

    @Schema(description = "规则组ID（前端传入的id即校验规则组ID，用于定位唯一该类型校验记录）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "规则组ID不能为空")
    private Long id;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户信息校验")
    @jakarta.validation.constraints.NotBlank(message = "规则组名称不能为空")
    private String rgName;

    @Schema(description = "父实体UUID（与entityId二选一）")
    private String entityUuid;

    @Schema(description = "父实体ID（兼容旧版，与entityUuid二选一）")
    private String entityId;

    @Schema(description = "子实体UUID（与childEntityId二选一）")
    private String childEntityUuid;

    @Schema(description = "子实体ID（兼容旧版，与childEntityUuid二选一）")
    private String childEntityId;

    @Schema(description = "是否启用", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "是否启用不能为空")
    private Integer isEnabled;

    @Schema(description = "提示信息", example = "子表数据不能为空")
    private String promptMessage;

    @Schema(description = "版本标识", example = "1")
    private Long versionTag;

    @Schema(description = "校验方式", example = "POP")
    private String valMethod;

    @Schema(description = "弹窗提示内容", example = "不满足条件，无法提交")
    private String popPrompt;

    @Schema(description = "弹窗类型", example = "SHORT")
    private String popType;
}

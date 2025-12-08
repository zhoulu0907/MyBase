package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 校验规则分组新增/修改 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验规则分组新增/修改 Request VO")
@Data
public class ValidationRuleGroupSaveReqVO {

    @Schema(description = "规则组编号（新增时不传，修改时必传）", example = "1024")
    private Long id;

    @Schema(description = "规则组UUID（更新时可用于定位记录）", example = "01onal1s-0000-0000-0000-000000000006")
    private String groupUuid;

    @Schema(description = "规则组名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "客户信用评级规则")
    @NotBlank(message = "规则组名称不能为空")
    @Size(max = 100, message = "规则组名称长度不能超过100个字符")
    private String rgName;

    @Schema(description = "规则组描述", example = "用于识别高价值潜力的客户")
    @Size(max = 500, message = "规则组描述长度不能超过500个字符")
    private String rgDesc;

    @Schema(description = "规则组状态，见 ValidationStatusEnum 枚举（1-激活，0-非激活）", example = "1")
    private Integer rgStatus;

    @Schema(description = "校验方式", example = "POP")
    private String valMethod;

    @Schema(description = "弹窗提示内容", example = "不满足条件，无法提交")
    private String popPrompt;

    @Schema(description = "弹窗类型", example = "SHORT")
    private String popType;

    @Schema(description = "校验类型：REQUIRED / UNIQUE / LENGTH / RANGE / FORMAT / CHILD_NOT_EMPTY / SELF_DEFINED", example = "REQUIRED")
    private String validationType;

    @Schema(description = "规则定义二维数组，外层数组元素间为OR关系，内层数组元素间为AND关系")
    @Valid
    private List<List<ValidationRuleDefinitionVO>> valueRules;

    /**
     * 业务实体UUID
     */
    @Schema(description = "业务实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String entityUuid;

    @Schema(description = "业务实体ID（兼容旧版，与entityUuid二选一）", example = "51515658843258880")
    private String entityId;

    @Schema(description = "应用ID", example = "165345881671991296")
    private Long applicationId;

    // ==================== 向后兼容方法 ====================

    /**
     * 设置业务实体ID（兼容旧代码）
     * @deprecated 请使用 setEntityId(String)
     */
    @Deprecated
    public void setEntityId(Long entityId) {
        this.entityId = entityId != null ? String.valueOf(entityId) : null;
    }

}

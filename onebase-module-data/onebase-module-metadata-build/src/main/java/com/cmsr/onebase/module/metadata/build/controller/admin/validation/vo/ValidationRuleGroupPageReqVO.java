package com.cmsr.onebase.module.metadata.build.controller.admin.validation.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台 - 校验规则分组分页 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 校验规则分组分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationRuleGroupPageReqVO extends PageParam {

    @Schema(description = "规则组名称", example = "客户信用评级规则")
    private String rgName;

    @Schema(description = "规则组状态", example = "ACTIVE")
    private String rgStatus;

    @Schema(description = "校验类型：REQUIRED / UNIQUE / LENGTH / RANGE / FORMAT / CHILD_NOT_EMPTY / SELF_DEFINED", example = "REQUIRED")
    private String validationType;

    @Schema(description = "业务实体UUID", example = "entity-xxxx-xxxx-xxxx")
    private String entityUuid;

    @Schema(description = "业务实体ID（兼容旧版，与entityUuid二选一）", example = "51515658843258880")
    private String entityId;

}

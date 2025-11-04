package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程预测请求VO
 *
 * @author liyang
 * @date 2025-11-04
 */
@Schema(description = "流程预测VO")
@Data
public class BpmPredictReqVO {
    @Schema(description = "业务ID")
    @NotNull(message = "业务ID不能为空")
    private Long businessId;

    /**
     * 实体信息
     */
    @Schema(description = "实体信息")
    private EntityVO entity;
}

package com.cmsr.onebase.module.bpm.core.vo.instance;

import com.cmsr.onebase.module.bpm.core.vo.EntityVO;
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
    @Schema(description = "业务UUID")
    @NotNull(message = "业务UUID不能为空")
    private String businessUuid;

    /**
     * 实体信息
     */
    @Schema(description = "实体信息")
    private EntityVO entity;
}

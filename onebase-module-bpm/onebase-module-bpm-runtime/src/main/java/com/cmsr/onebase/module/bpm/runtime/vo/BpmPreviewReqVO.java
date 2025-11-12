package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程预览请求VO
 *
 * @author liyang
 * @date 2025-11-11
 */
@Schema(description = "流程预览VO")
@Data
public class BpmPreviewReqVO {
    @Schema(description = "流程实例ID")
    @NotNull(message = "流程实例ID不能为空")
    private Long instanceId;
}

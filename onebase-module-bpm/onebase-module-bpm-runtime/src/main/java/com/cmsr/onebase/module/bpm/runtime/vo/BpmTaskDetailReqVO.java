package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程详情请求VO
 *
 * @author liyang
 * @date 2025-11-15
 */
@Schema(description = "流程详情请求VO")
@Data
public class BpmTaskDetailReqVO {
    /**
     * 待办任务ID
     *
     * 除了我的发起列表，理论上其他都需要携带该字段
     *
     * 点击详情后没有新的待办，则会根据taskId去查找之前的节点信息，以确定字段显隐
     *
     */
    @Schema(description = "待办任务ID")
    private Long taskId;

    @Schema(description = "流程实例ID")
    @NotNull(message = "流程实例ID不能为空")
    private Long instanceId;

    @Schema(description = "来源")
    private String from;
}

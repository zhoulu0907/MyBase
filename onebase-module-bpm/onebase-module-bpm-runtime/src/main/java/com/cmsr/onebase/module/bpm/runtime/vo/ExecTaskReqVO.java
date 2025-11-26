package com.cmsr.onebase.module.bpm.runtime.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程执行任务请求VO
 *
 * @author liyang
 * @date 2025/10/19
 */

@Data
@Schema(description = "流程执行操作按钮请求VO")
public class ExecTaskReqVO {

    /**
     * 按钮类型
     */
    @NotBlank(message = "按钮类型不能为空")
    private String buttonType;

    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    /**
     * 流程实例ID
     */
    @NotBlank(message = "流程实例ID不能为空")
    private String instanceId;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 实体数据
     */
    @Valid
    private EntityVO entity;
}
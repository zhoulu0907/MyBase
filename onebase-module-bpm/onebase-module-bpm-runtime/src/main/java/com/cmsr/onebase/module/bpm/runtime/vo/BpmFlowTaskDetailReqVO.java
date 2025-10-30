package com.cmsr.onebase.module.bpm.runtime.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BpmFlowTaskDetailReqVO {
    @NotBlank(message = "任务ID不能为空")
    private   String taskId;

    @NotBlank(message = "实例ID不能为空")
    private   String instanceId;

    @NotNull(message = "实体ID不能为空")
    private   Long entityId;

    @NotNull(message = "数据ID不能为空")
    private   Long dataId;
}

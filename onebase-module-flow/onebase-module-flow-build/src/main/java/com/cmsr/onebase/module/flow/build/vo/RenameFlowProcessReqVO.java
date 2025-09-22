package com.cmsr.onebase.module.flow.build.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 流程重命名请求VO
 */
@Data
public class RenameFlowProcessReqVO {

    /**
     * 流程ID
     */
    @NotNull(message = "流程ID不能为空")
    private Long id;

    /**
     * 新的流程名称
     */
    @NotBlank(message = "流程名称不能为空")
    private String processName;
}
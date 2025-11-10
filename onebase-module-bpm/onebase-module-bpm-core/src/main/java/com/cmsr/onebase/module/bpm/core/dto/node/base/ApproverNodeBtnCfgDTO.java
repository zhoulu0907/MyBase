package com.cmsr.onebase.module.bpm.core.dto.node.base;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 审批人节点按钮配置信息
 *
 * @author liyang
 * @date 2025-10-22
 */
@Data
public class ApproverNodeBtnCfgDTO extends BaseNodeBtnCfgDTO {
    /**
     * 是否开启批量审批
     */
    @NotNull(message = "是否开启批量审批不能为空")
    private Boolean batchApproval = false;
}

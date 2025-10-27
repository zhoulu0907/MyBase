package com.cmsr.onebase.module.bpm.api.dto.node.base;

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
    private Boolean batchApproval;
}

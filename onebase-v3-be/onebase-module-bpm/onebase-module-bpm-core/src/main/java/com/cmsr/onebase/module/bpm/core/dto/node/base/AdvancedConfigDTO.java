package com.cmsr.onebase.module.bpm.core.dto.node.base;

import com.cmsr.onebase.module.bpm.core.dto.BpmGlobalConfigDTO;
import com.cmsr.onebase.module.bpm.core.enums.BpmEmptyApproverEnum;
import lombok.Data;

/**
 * 高级设置
 *
 */
@Data
public class AdvancedConfigDTO {
    /**
     * 自动审批配置
     */
    private BpmGlobalConfigDTO.AutoApproveConfig autoApproveCfg = new BpmGlobalConfigDTO.AutoApproveConfig();

    /**
     * 审批人为空处理配置
     */
    private BpmGlobalConfigDTO.EmptyApproverConfig emptyApproverCfg = new BpmGlobalConfigDTO.EmptyApproverConfig();


}

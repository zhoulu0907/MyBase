package com.cmsr.onebase.module.bpm.core.dto.node.base;

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
    private AutoApproveConfig autoApproveCfg = new AutoApproveConfig();

    /**
     * 审批人为空处理配置
     */
    private EmptyApproverConfig emptyApproverCfg = new EmptyApproverConfig();


    @Data
    public static class AutoApproveConfig {
        /**
         * 发起人自动审批
         * 当前节点人员为发起人时，自动审批
         */
        private Boolean initAutoApprove = false;

        /**
         * 重复人员自动审批
         * 当前节点人员已在前面审批节点中进行过操作时，自动审批
         */
        private Boolean dupUserAutoApprove = false;

        /**
         * 上一节点重复人员自动审批
         * 当前节点人员已在上一审批节点中进行过操作时，自动审批
         */
        private Boolean prevNodeDupUserAutoApprove = false;
    }

    /**
     * 审批人为空处理配置
     *
     * @author liyang
     * @date 2025-10-21
     */
    @Data
    public static class EmptyApproverConfig {
        /**
         * 审批人为空时的处理方式
         * 可选值：pause(流程暂停)、skip(自动跳过节点)、transfer_admin(转交给应用管理员)、transfer_member(转交给指定成员)
         */
        private String handlerMode = BpmEmptyApproverEnum.PAUSE.getCode();

        /**
         * 转交给指定成员时的成员ID
         * 当handlerMode为transfer_member时，此字段必填
         */
        private Long transferMemberId;
    }
}

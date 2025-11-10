package com.cmsr.onebase.module.bpm.core.dto;

import com.cmsr.onebase.module.bpm.core.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 流程全局设置
 *
 * @author liyang
 * @date 2025/10/24
 */
@Data
public class BpmGlobalConfigDTO {
    /**
     * 是否使用节点配置
     */
    @NotNull(message = "是否使用节点配置不能为空")
    private Boolean useNodeConfig = false;

    /**
     * 自动审批配置
     */
    private AutoApproveConfig autoApproveCfg = new AutoApproveConfig();

    /**
     * 审批人为空处理配置
     */
    private EmptyApproverConfig emptyApproverCfg = new EmptyApproverConfig();

    /**
     * 流程撤回规则配置
     */
    private WithdrawRuleConfig withdrawRuleCfg = new WithdrawRuleConfig();

    /**
     * 流程退回规则配置
     */
    private ReturnRuleConfig returnRuleCfg = new ReturnRuleConfig();

    /**
     * 流程发起人终止权限配置
     */
    private InitiatorTerminateConfig initiatorTerminateCfg = new InitiatorTerminateConfig();

    /**
     * 表单摘要配置
     */
    private FormSummaryConfig formSummaryCfg;

    /**
     * 自动审批配置
     *
     * @author liyang
     * @date 2025-10-21
     */
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

    /**
     * 流程撤回规则配置
     *
     * @author liyang
     * @date 2025-10-21
     */
    @Data
    public static class WithdrawRuleConfig {
        /**
         * 撤回权限
         */
        private String permission = BpmWithdrawPermEnum.NONE.getCode();

        /**
         * 撤回时机
         */
        private String timing = BpmWithdrawTimingEnum.UNPROCESSED.getCode();
    }

    /**
     * 流程退回规则配置
     *
     * @author liyang
     * @date 2025-10-21
     */
    @Data
    public static class ReturnRuleConfig {
        /**
         * 退回规则
         */
        private String rule = BpmReturnRuleEnum.SEQ.getCode();
    }

    /**
     * 流程发起人终止权限配置
     *
     * @author liyang
     * @date 2025-10-21
     */
    @Data
    public static class InitiatorTerminateConfig {
        /**
         * 终止权限
         *
         */
        private String permission = BpmInitiatorTermPermEnum.INITIATION_NODE.getCode();
    }

    /**
     * 表单摘要配置
     *
     * @author liyang
     * @date 2025-10-21
     */
    @Data
    public static class FormSummaryConfig {
        /**
         * 字段配置列表
         */
        private List<FieldConfigDTO> fieldConfigs;
    }

    /**
     * 字段配置DTO
     *
     * @author liyang
     * @date 2025-10-21
     */
    @Data
    public static class FieldConfigDTO {
        /**
         * 字段ID
         */
        @NotNull(message = "字段ID不能为空")
        private Long fieldId;

        /**
         * 字段名
         */
        @NotBlank(message = "字段名不能为空")
        private String fieldName;
    }
}

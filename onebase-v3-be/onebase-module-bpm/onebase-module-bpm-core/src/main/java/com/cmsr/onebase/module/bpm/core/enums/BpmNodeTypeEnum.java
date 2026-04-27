package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.warm.flow.core.enums.NodeType;

/**
 * 流程节点类型枚举
 *
 * @author liyang
 * @date 2025-10-20
 */
@Getter
@AllArgsConstructor
public enum BpmNodeTypeEnum {

    // ============ 开始节点 ==================

    /**
     * 流程开始节点
     *
     */
    START("start", "流程开始", "开始"),

    /**
     * 每个审批流程都会默认拥有一个发起节点，该节点不可删除，可配置流程发起人所拥有的表单字段权限
     */
    INITIATION("initiation", "发起", "流程发起"),

    // ============ 人工节点 ==================

    /**
     * 审批人节点
     * 处理审批任务，需要做出同意、拒绝、转交、加签、退回等决策，一个流程至少包含一个审批节点
     */
    APPROVER("approver", "审批人", "处理审批任务，需要做出同意、拒绝、转交、加签、退回等决策"),

    /**
     * 抄送人节点
     * 用于在审批人审批后给抄送人发送消息提醒，抄送人无需审批和执行
     */
    CC("cc", "抄送人", "用于在审批人审批后给抄送人发送消息提醒，抄送人无需审批和执行"),

    /**
     * 执行人节点
     * 无需决策，只需执行工作，然后返回流程审批人继续处理，执行人做的工作与审批无关
     */
    EXECUTOR("executor", "执行人", "无需决策，只需执行工作，然后返回流程审批人继续处理"),

    // ============ 分支节点 ==================

    /**
     * 条件分支节点
     * 根据条件表达式的结果，从多个分支中选择唯一一条满足条件的路径继续执行，其余分支被跳过
     */
    CONDITION("condition", "条件分支", "根据条件表达式的结果，从多个分支中选择唯一一条满足条件的路径继续执行"),

    /**
     * 并行分支节点
     * 根据条件决定哪些分支可以执行（可一个或多个），允许多条符合条件的分支并发运行
     */
    PARALLEL("parallel", "并行分支", "根据条件决定哪些分支可以执行，允许多条符合条件的分支并发运行"),

    /**
     * 汇聚节点
     * 等待所有来自上游的并行或条件分支完成并汇集到此点后，流程才能继续向下执行
     */
    JOIN("join", "汇聚节点", "等待所有来自上游的并行或条件分支完成并汇集到此点后，流程才能继续向下执行"),

    // ============ 消息通知节点 ==================

    /**
     * 消息通知节点
     * 在流程执行到某个节点时，通过已经配置完成的消息模板，通过指定渠道向指定人员发送站内信
     */
    MESSAGE_NOTIFY("message_notify", "消息通知", "在流程执行到某个节点时，通过已经配置完成的消息模板，通过指定渠道向指定人员发送站内信"),

    // ============ 逻辑节点 ==================

    /**
     * 子流程节点
     * 当业务流程非常复杂时，可以将流程拆分为一条父流程线和一条或多条子流程线去执行
     */
    SUB_PROCESS("sub_process", "子流程", "当业务流程非常复杂时，可以将流程拆分为一条父流程线和一条或多条子流程线去执行"),

    /**
     * 自动化节点
     * 调用自动化工作流的节点
     */
    SERVICE_TASK("service_task", "自动化节点", "调用自动化工作流的节点"),

    /**
     * 任务节点
     * 分发给指定的地址一个任务，等任务完成后需要手动调用 API 来完成执行，一般用于第三方平台对接
     */
    EXTERNAL_TASK("external_task", "任务节点", "分发给指定的地址一个任务，等任务完成后需要手动调用 API 来完成执行"),

    /**
     * 延时节点
     * 事件延时节点，用于审批后隔一段时间再进行后续处理，核心点在于延时节点不计算超时时间
     */
    DELAY("delay", "延时节点", "事件延时节点，用于审批后隔一段时间再进行后续处理，核心点在于延时节点不计算超时时间"),

    // ============ 结束节点 ==================

    /**
     * 流程结束节点
     * 流程的终止节点
     */
    END("end", "流程结束", "流程的终止节点");

    /**
     * 节点编码
     */
    private final String code;

    /**
     * 节点名称
     */
    private final String name;

    /**
     * 节点作用描述
     */
    private final String description;

    /**
     * 根据编码获取节点类型
     *
     * @param code 节点编码
     * @return BpmNodeTypeEnum
     */
    public static BpmNodeTypeEnum getByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

        String lowerCode = code.toLowerCase();

        for (BpmNodeTypeEnum nodeType : values()) {
            if (nodeType.getCode().equals(lowerCode)) {
                return nodeType;
            }
        }

        return null;
    }

    /**
     * 转换为WarmFlow的NodeType
     * 将业务节点类型映射到WarmFlow的基础节点类型
     *
     * @return NodeType
     */
    public NodeType toWarmFlowNodeType() {
        switch (this) {
            case START:
                return NodeType.START;
            case END:
                return NodeType.END;
            case CONDITION:
                // 条件分支对应互斥网关
                return NodeType.SERIAL;
            case PARALLEL:
                // 并行分支对应并行网关
            case JOIN:
                // 汇聚节点也对应并行网关
                return NodeType.PARALLEL;
            case INITIATION:
            case APPROVER:
            case CC:
            case EXECUTOR:
            case MESSAGE_NOTIFY:
            case SUB_PROCESS:
            case SERVICE_TASK:
            case EXTERNAL_TASK:
            case DELAY:
            default:
                // 其他节点都作为中间节点处理
                return NodeType.BETWEEN;
        }
    }

    public static NodeType toWarmFlowNodeType(String code) {
        BpmNodeTypeEnum nodeTypeEnum = getByCode(code);

        if (nodeTypeEnum != null) {
            return nodeTypeEnum.toWarmFlowNodeType();
        }

        return null;
    }
}

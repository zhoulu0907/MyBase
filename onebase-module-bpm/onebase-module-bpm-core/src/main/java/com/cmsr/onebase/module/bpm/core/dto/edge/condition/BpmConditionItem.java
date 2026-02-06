package com.cmsr.onebase.module.bpm.core.dto.edge.condition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 *
 * 流程条件项
 *
 * @author liyang
 * @date 2025-11-25
 *
 */
@Data
public class BpmConditionItem {
    /**
     * 业务字段类型
     * pre_node 上个审批节点属性
     * instance 流程实例属性
     * entity 表单字段
     */
    @NotBlank(message = "业务字段类型不能为空")
    private String fieldScope;

    /** fieldScope选择上个审批节点属性时，fieldName对应的枚举

    审批结果（即节点处理操作）   approvalResult     下拉框
    审批人ID                  approverId        唯一标识
    审批时间（即节点处理时间）   approvalTime      日期时间
    审批人部门ID              approverDeptId     唯一标识

        ### fieldScope选择流程实例属性时，fieldName对应的枚举
    流程标题        bpmTitle    文本框
    发起人ID        initiatorId  唯一标识
    发起部门ID      initiatorDeptId 唯一标识
    发起时间      submitTime     日期时间
    创建时间      createTime   日期时间
    更新时间    updateTime    日期时间

    fieldScope选择表单字段时，fieldName对应的是表字段名
     */
    @NotBlank(message = "fieldName不能为空")
    private String fieldName;

    /** 操作符枚举即 op对应OpEnum枚举
    选择的字段非时间类型时，枚举如下
    EQUALS 等于 NOT_EQUALS 不等于 CONTAINS包含 NOT_CONTAINS不包含 EXISTS_IN 存在于
    NOT_EXISTS_IN 不存在于 IS_EMPTY为空
            IS_NOT_EMPTY不为空
    选择的字段是时间类型时，枚举如下
    GREATER_THAN 大于 GREATER_EQUALS大于等于 LESS_THAN小于
    LESS_EQUALS小于等于 LATER_THAN 晚于 EARLIER_THAN早于 RANGE 范围  CONTAINS_ALL 包含全部 NOT_CONTAINS_ALL 不包含全部
    CONTAINS_ANY 包含任一 NOT_CONTAINS_ANY 不包含任一
     */
    @NotBlank(message = "业务字段操作符不能为空")
    private String op;

//    operatorType对应枚举为 OperatorTypeEnum
    @NotBlank(message = "操作类型不能为空")
    private String operatorType;

    // 对应的是SemanticFieldTypeEnum
    @NotBlank(message = "业务字段类型不能为空")
    private String fieldType;

    /**
     * 如果是operatorType是值，value可能是字符串，也可能是数组，也可能是Map。
     * 如果是operatorType是变量，value是变字符串
     * 如果是operatorType是公式，value则为对象
     *
     */
    @NotNull(message = "业务字段值不能为空")
    private Object value;
}

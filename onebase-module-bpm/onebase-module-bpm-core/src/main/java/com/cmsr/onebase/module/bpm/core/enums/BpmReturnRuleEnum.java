package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程退回规则枚举
 *
 * @author liyang
 * @date 2025-11-04
 */
@Getter
@AllArgsConstructor
public enum BpmReturnRuleEnum {
    /**
     * 按顺序重新审批
     */
    SEQ("seq","按流程顺序重新审批"),
    /**
     * 直达当前节点
     */
    DIRECT("direct","直达当前节点");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    public static BpmReturnRuleEnum getByCode(String code) {
        for (BpmReturnRuleEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

package com.cmsr.onebase.module.bpm.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
/**
 * 全局配置流程退回规则枚举
 */
public enum GlobalFlowReturnRulesEnum {
    /**
     * 按顺序重新审批
     */
    SEQUENTIAL("sequential","按流程顺序重新审批"),
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

    public static GlobalFlowReturnRulesEnum getByCode(String code) {
        for (GlobalFlowReturnRulesEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

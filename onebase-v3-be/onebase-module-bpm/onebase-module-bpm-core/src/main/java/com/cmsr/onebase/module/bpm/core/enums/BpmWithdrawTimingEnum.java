package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程撤回时机枚举
 *
 * @author liyang
 * @date 2025-11-04
 */
@Getter
@AllArgsConstructor
public enum BpmWithdrawTimingEnum {
    /**
     * 未操作
     * 下一人工节点处理人未进行流程处理操作
     */
    UNPROCESSED("unprocessed", "未操作"),

    /**
     * 未读
     * 下一人工节点处理人未查看详情
     */
    UNREAD("unread", "未读");

    /**
     * 类型编码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static BpmWithdrawTimingEnum getByCode(String code) {
        for (BpmWithdrawTimingEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}


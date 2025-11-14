package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抄送人类型枚举
 *
 * @author liyang
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum CopyReceiverTypeEnum {

    /**
     * 指定成员
     * 可以指定某位成员来进行抄送，需额外选择抄送人（必填），点击后弹出人员选择窗口
     */
    USER("user", "指定成员"),

    /**
     * 指定角色
     * 指定某个角色例如人事、行政、财务等进行抄送
     */
    ROLE("role", "指定角色");

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
    public static CopyReceiverTypeEnum getByCode(String code) {
        for (CopyReceiverTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

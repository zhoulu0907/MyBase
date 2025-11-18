package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 抄送查看状态枚举
 *
 * @author may
 */
@Getter
@AllArgsConstructor
public enum BpmCcViewStatusEnum {

    /**
     * 已读
     */
    VIEWED("viewed", "已读"),

    /**
     * 未读
     */
    UNVIEWED("unviewed", "未读");

    /**
     * 状态
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;


    /**
     * 根据状态获取枚举
     *
     * @param code 状态
     * @return 枚举
     */
    public static BpmCcViewStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }

        for (BpmCcViewStatusEnum button : values()) {
            if (button.code.equals(code)) {
                return button;
            }
        }
        return null;
    }

}

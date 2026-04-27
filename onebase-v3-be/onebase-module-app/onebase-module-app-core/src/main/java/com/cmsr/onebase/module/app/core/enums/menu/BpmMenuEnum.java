package com.cmsr.onebase.module.app.core.enums.menu;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author liyang
 *
 * @date 2025-11-22
 */
@Getter
@AllArgsConstructor
public enum BpmMenuEnum {
    /**
     * 待办
     */
    TODO("TASK-ineedtodo", "待我处理"),

    /**
     * 已办
     */
    DONE("TASK-ihavedone", "我已处理"),

    /**
     * 我创建的
     */
    CREATED("TASK-icreated", "我创建的"),

    /**
     * 抄送
     */
    CC("TASK-icopied", "抄送我的"),

    /**
     * 流程代理
     */
    AGENT("TASK-taskproxy", "流程代理"),

    ;

    private final String code;

    private final String text;

    /**
     * 根据code获取枚举
     *
     * @param code 菜单编码
     * @return BpmMenuEnum
     */
    public static BpmMenuEnum getByCode(String code) {
        if (code == null) {
            return null;
        }

        for (BpmMenuEnum menuEnum : values()) {
            if (menuEnum.getCode().equals(code)) {
                return menuEnum;
            }
        }

        return null;
    }
}

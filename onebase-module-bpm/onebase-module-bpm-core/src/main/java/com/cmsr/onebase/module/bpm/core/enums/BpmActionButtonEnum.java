package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * BPM流程操作按钮枚举
 *
 * 定义流程任务中可执行的操作按钮类型
 *
 * @author liyang
 * @date 2025-10-27
 */
@Getter
@AllArgsConstructor
public enum BpmActionButtonEnum {

    /**
     * 同意
     */
    APPROVE("approve", "同意"),

    /**
     * 拒绝
     */
    REJECT("reject", "拒绝"),

    /**
     * 保存
     */
    SAVE("save", "保存"),

    /**
     * 转交
     */
    TRANSFER("transfer", "转交"),

    /**
     * 加签
     */
    ADD_SIGN("add_sign", "加签"),

    /**
     * 退回
     */
    RETURN("return", "退回"),

    /**
     * 撤回
     */
    WITHDRAW("withdraw", "撤回"),

    /**
     * 弃权
     */
    ABSTAIN("abstain", "弃权"),

    /**
     * 提交目前只有在发起节点时有效
     */
    SUBMIT("submit", "提交"),
    ;

    /**
     * 按钮编码
     */
    private final String code;

    /**
     * 按钮名称
     */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 按钮编码
     * @return 按钮枚举
     */
    public static BpmActionButtonEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (BpmActionButtonEnum button : values()) {
            if (button.code.equals(code)) {
                return button;
            }
        }
        return null;
    }

    /**
     * 判断编码是否存在
     *
     * @param code 按钮编码
     * @return 是否存在
     */
    public static boolean exists(String code) {
        return getByCode(code) != null;
    }
}

package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Getter
@AllArgsConstructor
public enum CommonStatusEnum {

    /**
     * 启用/开启
     */
    ENABLED(1, "启用"),

    /**
     * 禁用/关闭
     */
    DISABLED(0, "禁用");

    /**
     * 状态值
     */
    private final Integer status;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据状态值获取枚举
     *
     * @param status 状态值
     * @return 枚举对象
     */
    public static CommonStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (CommonStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否启用
     *
     * @param status 状态值
     * @return 是否启用
     */
    public static boolean isEnabled(Integer status) {
        return ENABLED.getStatus().equals(status);
    }

    /**
     * 判断是否禁用
     *
     * @param status 状态值
     * @return 是否禁用
     */
    public static boolean isDisabled(Integer status) {
        return DISABLED.getStatus().equals(status);
    }

    /**
     * 根据布尔值获取对应的状态值
     *
     * @param boolValue 布尔值
     * @return 对应的状态值（true返回1，false返回0）
     */
    public static Integer getStatusValue(boolean boolValue) {
        return boolValue ? ENABLED.getStatus() : DISABLED.getStatus();
    }

    /**
     * 根据输入值判断是否启用，并返回对应的状态值
     *
     * @param value 输入值
     * @return 对应的状态值（值启用返回1，否则返回0）
     */
    public static Integer toStatusValue(Integer value) {
        return isEnabled(value) ? ENABLED.getStatus() : DISABLED.getStatus();
    }

    /**
     * 根据输入值判断是否启用（取反），并返回对应的状态值
     *
     * @param value 输入值
     * @return 对应的状态值（值禁用返回1，否则返回0）
     */
    public static Integer toInverseStatusValue(Integer value) {
        return isDisabled(value) ? ENABLED.getStatus() : DISABLED.getStatus();
    }
}

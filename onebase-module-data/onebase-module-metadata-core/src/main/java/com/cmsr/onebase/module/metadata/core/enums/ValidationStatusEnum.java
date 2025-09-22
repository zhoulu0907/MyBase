package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 校验状态枚举
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Getter
@AllArgsConstructor
public enum ValidationStatusEnum {

    /**
     * 激活/启用
     */
    ACTIVE(1, "激活"),

    /**
     * 非激活/禁用
     */
    INACTIVE(0, "非激活");

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
    public static ValidationStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (ValidationStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否激活
     *
     * @param status 状态值
     * @return 是否激活
     */
    public static boolean isActive(Integer status) {
        return ACTIVE.getStatus().equals(status);
    }

    /**
     * 判断是否非激活
     *
     * @param status 状态值
     * @return 是否非激活
     */
    public static boolean isInactive(Integer status) {
        return INACTIVE.getStatus().equals(status);
    }

    /**
     * 根据布尔值获取对应的状态值
     *
     * @param boolValue 布尔值
     * @return 对应的状态值（true返回1，false返回0）
     */
    public static Integer getStatusValue(boolean boolValue) {
        return boolValue ? ACTIVE.getStatus() : INACTIVE.getStatus();
    }

    /**
     * 根据输入值判断是否激活，并返回对应的状态值
     *
     * @param value 输入值
     * @return 对应的状态值（值激活返回1，否则返回0）
     */
    public static Integer toStatusValue(Integer value) {
        return isActive(value) ? ACTIVE.getStatus() : INACTIVE.getStatus();
    }

    /**
     * 根据输入值判断是否激活（取反），并返回对应的状态值
     *
     * @param value 输入值
     * @return 对应的状态值（值非激活返回1，否则返回0）
     */
    public static Integer toInverseStatusValue(Integer value) {
        return isInactive(value) ? ACTIVE.getStatus() : INACTIVE.getStatus();
    }
}

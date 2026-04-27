package com.cmsr.onebase.module.metadata.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 布尔状态枚举（是/否）
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Getter
@AllArgsConstructor
public enum BooleanStatusEnum {

    /**
     * 是
     */
    YES(1, "是"),

    /**
     * 否
     */
    NO(0, "否");

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
    public static BooleanStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (BooleanStatusEnum statusEnum : values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }

    /**
     * 判断是否为是
     *
     * @param status 状态值
     * @return 是否为是
     */
    public static boolean isYes(Integer status) {
        return YES.getStatus().equals(status);
    }

    /**
     * 判断是否为否
     *
     * @param status 状态值
     * @return 是否为否
     */
    public static boolean isNo(Integer status) {
        return NO.getStatus().equals(status);
    }

    /**
     * 根据布尔值获取对应的状态值
     *
     * @param boolValue 布尔值
     * @return 对应的状态值（true返回1，false返回0）
     */
    public static Integer getStatusValue(boolean boolValue) {
        return boolValue ? YES.getStatus() : NO.getStatus();
    }

    /**
     * 根据输入值判断是否为真，并返回对应的状态值
     *
     * @param value 输入值
     * @return 对应的状态值（值为真返回1，否则返回0）
     */
    public static Integer toStatusValue(Integer value) {
        return isYes(value) ? YES.getStatus() : NO.getStatus();
    }

    /**
     * 根据输入值判断是否为真（取反），并返回对应的状态值
     *
     * @param value 输入值
     * @return 对应的状态值（值为假返回1，否则返回0）
     */
    public static Integer toInverseStatusValue(Integer value) {
        return isNo(value) ? YES.getStatus() : NO.getStatus();
    }
}

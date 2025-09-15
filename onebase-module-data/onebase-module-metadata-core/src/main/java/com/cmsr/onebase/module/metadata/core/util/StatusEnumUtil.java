package com.cmsr.onebase.module.metadata.core.util;

import com.cmsr.onebase.module.metadata.core.enums.BooleanStatusEnum;
import com.cmsr.onebase.module.metadata.core.enums.CommonStatusEnum;
import com.cmsr.onebase.module.metadata.core.enums.ValidationStatusEnum;

/**
 * 状态枚举工具类
 *
 * @author matianyu
 * @date 2025-08-20
 */
public class StatusEnumUtil {

    /**
     * 通用状态 - 启用
     */
    public static final Integer ENABLED = CommonStatusEnum.ENABLED.getStatus();

    /**
     * 通用状态 - 禁用
     */
    public static final Integer DISABLED = CommonStatusEnum.DISABLED.getStatus();

    /**
     * 布尔状态 - 是
     */
    public static final Integer YES = BooleanStatusEnum.YES.getStatus();

    /**
     * 布尔状态 - 否
     */
    public static final Integer NO = BooleanStatusEnum.NO.getStatus();

    /**
     * 校验状态 - 激活
     */
    public static final Integer ACTIVE = ValidationStatusEnum.ACTIVE.getStatus();

    /**
     * 校验状态 - 非激活
     */
    public static final Integer INACTIVE = ValidationStatusEnum.INACTIVE.getStatus();

    /**
     * 判断通用状态是否启用
     *
     * @param status 状态值
     * @return 是否启用
     */
    public static boolean isEnabled(Integer status) {
        return CommonStatusEnum.isEnabled(status);
    }

    /**
     * 判断通用状态是否禁用
     *
     * @param status 状态值
     * @return 是否禁用
     */
    public static boolean isDisabled(Integer status) {
        return CommonStatusEnum.isDisabled(status);
    }

    /**
     * 判断布尔状态是否为是
     *
     * @param status 状态值
     * @return 是否为是
     */
    public static boolean isYes(Integer status) {
        return BooleanStatusEnum.isYes(status);
    }

    /**
     * 判断布尔状态是否为否
     *
     * @param status 状态值
     * @return 是否为否
     */
    public static boolean isNo(Integer status) {
        return BooleanStatusEnum.isNo(status);
    }

    /**
     * 判断校验状态是否激活
     *
     * @param status 状态值
     * @return 是否激活
     */
    public static boolean isActive(Integer status) {
        return ValidationStatusEnum.isActive(status);
    }

    /**
     * 判断校验状态是否非激活
     *
     * @param status 状态值
     * @return 是否非激活
     */
    public static boolean isInactive(Integer status) {
        return ValidationStatusEnum.isInactive(status);
    }
}

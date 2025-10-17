package com.cmsr.onebase.framework.dolphins.dto.schedule.enums;

/**
 * 告警类型枚举
 *
 * 对应 swagger 中的 warningType
 *
 * @author matianyu
 * @date 2025-10-17
 */
public enum WarningTypeEnum {
    /** 不告警 */
    NONE,
    /** 成功时告警 */
    SUCCESS,
    /** 失败时告警 */
    FAILURE,
    /** 所有情况都告警 */
    ALL
}

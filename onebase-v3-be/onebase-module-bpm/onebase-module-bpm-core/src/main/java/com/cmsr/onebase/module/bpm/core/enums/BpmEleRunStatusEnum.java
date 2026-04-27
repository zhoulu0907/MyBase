package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.warm.flow.core.enums.ChartStatus;

import java.util.Objects;

/**
 * 流程版本状态枚举
 *
 * @author liyang
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum BpmEleRunStatusEnum {

    /**
     * 未流转
     */
    PENDING("pending", "未流转"),

    /**
     * 处理中
     */
    PROCESSING("processing", "处理中"),

    /**
     * 已流转
     */
    COMPLETED("completed", "已流转");

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
    public static BpmEleRunStatusEnum getByCode(String code) {
        for (BpmEleRunStatusEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 图表状态转换为流程元素运行状态
     *
     * @param chartStatus 图表状态
     * @return 流程元素运行状态
     */
    public static BpmEleRunStatusEnum chartStatusToEleRunStatus(Integer chartStatus) {
        if (Objects.equals(ChartStatus.NOT_DONE.getKey(), chartStatus)) {
            return PENDING;
        } else if (Objects.equals(ChartStatus.TO_DO.getKey(), chartStatus)) {
            return PROCESSING;
        } else if (Objects.equals(ChartStatus.DONE.getKey(), chartStatus)) {
            return COMPLETED;
        }

        return null;
    }
}

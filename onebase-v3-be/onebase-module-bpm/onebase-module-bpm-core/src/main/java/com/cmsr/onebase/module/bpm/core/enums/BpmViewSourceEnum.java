package com.cmsr.onebase.module.bpm.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * 流程视图来源类型
 *
 * @author liyang
 * @date 2025-11-15
 */
@Getter
@AllArgsConstructor
public enum BpmViewSourceEnum {

    /**
     * 待办：当前用户有待办任务
     */
    TODO("todo", "待办"),

    /**
     * 已办：当前用户已完成的任务
     */
    DONE("done", "已办"),

    /**
     * 我的创建：流程发起人查看自己发起的流程
     */
    CREATED("created", "我的创建"),

    /**
     * 抄送：当前用户被抄送
     */
    CC("cc", "抄送"),

    /**
     * 抄送：当前用户被抄送
     */
    LIST("list", "列表");


    /**
     * 前端/URL 中使用的 code（小写，兼容现有参数）
     */
    private final String code;

    /**
     * 中文描述（可用于日志、提示）
     */
    private final String desc;

    public static BpmViewSourceEnum getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }

        String lowerCode = code.toLowerCase();

        for (BpmViewSourceEnum sourceEnum : values()) {
            if (sourceEnum.getCode().equals(lowerCode)) {
                return sourceEnum;
            }
        }

        return null;
    }
}
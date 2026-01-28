package com.cmsr.onebase.module.app.core.enums.app;

import java.util.Arrays;

/**
 * 导出状态枚举
 *
 * @author zhoumingji
 * @date 2026-01-26
 */
public enum AppExportStatusEnum {

    /**
     * 未知
     */
    UNKNOWN(0, "未知"),

    /**
     * 导出中
     */
    EXPORTING(1, "导出中"),

    /**
     * 导出成功
     */
    SUCCESS(2, "导出成功"),

    /**
     * 导出失败
     */
    FAILED(3, "导出失败");

    private final Integer value;

    private final String text;

    AppExportStatusEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    /**
     * 根据值获取枚举
     *
     * @param value 状态值
     * @return 枚举
     */
    public static AppExportStatusEnum getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(item -> item.value.equals(value))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据值获取文本
     *
     * @param value 状态值
     * @return 文本
     */
    public static String getText(Integer value) {
        AppExportStatusEnum status = getByValue(value);
        return status != null ? status.getText() : "未知";
    }
}

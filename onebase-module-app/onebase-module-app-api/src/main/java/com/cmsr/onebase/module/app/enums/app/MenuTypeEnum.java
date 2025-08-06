package com.cmsr.onebase.module.app.enums.app;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;

/**
 * @Author：huangjie
 * @Date：2025/7/30 16:21
 */
public enum MenuTypeEnum {

    PAGE(1, "页面"),

    GROUP(2, "目录");

    private final Integer value;

    private final String text;

    MenuTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }


    public static MenuTypeEnum getByValue(Integer value) {
        for (MenuTypeEnum menuTypeEnum : values()) {
            if (menuTypeEnum.getValue().equals(value)) {
                return menuTypeEnum;
            }
        }
        throw ServiceExceptionUtil.exception(AppErrorCodeConstants.MENU_TYPE_ERROR);
    }

    /**
     * 根据value判断值是否正确，不正确抛出异常
     */
    public static void validate(Integer value) {
        for (MenuTypeEnum item : values()) {
            if (item.getValue().equals(value)) {
                return;
            }
        }
        throw ServiceExceptionUtil.exception(AppErrorCodeConstants.MENU_TYPE_ERROR);
    }
}

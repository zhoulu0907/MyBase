package com.cmsr.onebase.module.app.core.enums.resource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.cmsr.onebase.module.app.core.enums.AppErrorCodeConstants;

/**
 * @Author：mickey.zhou
 * @Date：2025/10/29 16:21
 */
public enum PageTypeEnum {

    PAGE(1, "普通表单"),

    GROUP(2, "流程表单");

    private final Integer value;

    private final String text;

    PageTypeEnum(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }


    public static PageTypeEnum getByValue(Integer value) {
        for (PageTypeEnum menuTypeEnum : values()) {
            if (menuTypeEnum.getValue().equals(value)) {
                return menuTypeEnum;
            }
        }
        throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_TYPE_ERROR);
    }

    /**
     * 根据value判断值是否正确，不正确抛出异常
     */
    public static void validate(Integer value) {
        for (PageTypeEnum item : values()) {
            if (item.getValue().equals(value)) {
                return;
            }
        }
        throw ServiceExceptionUtil.exception(AppErrorCodeConstants.APP_MENU_TYPE_ERROR);
    }

    public static boolean isPage(Integer menuType) {
        return PAGE.getValue().equals(menuType);
    }

    public static boolean isGroup(Integer menuType) {
        return GROUP.getValue().equals(menuType);
    }

}

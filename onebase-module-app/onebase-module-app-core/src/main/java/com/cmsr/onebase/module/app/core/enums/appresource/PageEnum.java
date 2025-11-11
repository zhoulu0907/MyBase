package com.cmsr.onebase.module.app.core.enums.appresource;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;

/**
 * @Author：mickey.zhou
 *                     @Date：2025/7/30 16:21
 */
public enum PageEnum {

    FORM("form", "表单"),

    LIST("list", "列表"),
    WORKBENCH("workbench", "工作台");

    private final String value;

    private final String text;

    PageEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    /**
     * 根据value判断值是否正确，不正确抛出异常
     */
    public static void validate(String value) {
        for (PageEnum item : values()) {
            if (item.getValue().equals(value)) {
                return;
            }
        }
        throw ServiceExceptionUtil.exception(AppResourceErrorCodeConstants.PAGE_TYPE_ERROR);
    }

}

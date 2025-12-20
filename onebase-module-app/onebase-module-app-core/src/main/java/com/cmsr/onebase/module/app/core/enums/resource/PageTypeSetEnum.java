package com.cmsr.onebase.module.app.core.enums.resource;

/**
 * 页面集枚举定义
 */
public enum PageTypeSetEnum {
    //1. 普通表单 2. 流程表单 3. 工作台

    NORMAL_FORM(1, "普通表单"),
    PROCESS_FORM(2, "流程表单"),
    WORKBENCH(3, "工作台");
    private final Integer code;
    private final String description;
    PageTypeSetEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public Integer getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }

    //判断如果是工作台类型
    public static boolean isWorkBenchType(Integer code) {
        return WORKBENCH.getCode().equals(code);
    }

}

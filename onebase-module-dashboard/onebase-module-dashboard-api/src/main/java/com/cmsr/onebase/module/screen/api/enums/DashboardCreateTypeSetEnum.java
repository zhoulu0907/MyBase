package com.cmsr.onebase.module.screen.api.enums;

/**
 * 新建大屏类型枚举定义
 */
public enum DashboardCreateTypeSetEnum {

    DASHBOARD_NEW("dashboardNew", "空白页创建"),
    DASHBOARD_TEMPLATE("dashboardTemplate", "模板创建"),
    DASHBOARD_LINK("dashboardLink", "绑定现有大屏");
    private final String code;
    private final String description;
    DashboardCreateTypeSetEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String getCode() {
        return code;
    }
    public String getDescription() {
        return description;
    }


}

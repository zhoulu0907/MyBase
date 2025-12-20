package com.cmsr.onebase.module.app.core.enums.resource;


public enum WorkbenchComponentEnum {

    /**
     * 1. 快捷入口 2.待办中心 3.待办列表 4. 欢迎卡片 5. 轮播图 6. 资讯列表 7.数据列表 8. 富文本 9. 数据卡片
     */
    QUICK_ENTRY("quick_entry", "快捷入口"),
    TODO_CENTER("todo_center", "待办中心"),
    TODO_LIST("todo_list", "待办列表"),
    WELCOME_CARD("welcome_card", "欢迎卡片"),
    CAROUSEL("carousel", "轮播图"),
    NEWS_LIST("news_list", "资讯列表"),
    DATA_LIST("data_list", "数据列表"),
    RICH_TEXT("rich_text", "富文本"),
    DATA_CARD("data_card", "数据卡片"),
    ;
    private final String code;
    private final String description;
    WorkbenchComponentEnum(String code, String description) {
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

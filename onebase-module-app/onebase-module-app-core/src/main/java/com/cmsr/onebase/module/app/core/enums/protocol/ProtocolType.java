package com.cmsr.onebase.module.app.core.enums.protocol;

/**
 * @ClassName Dict
 * @Description 应用协议字典枚举
 * @Author mickey
 * @Date 2025/7/30 13:57
 */
public enum ProtocolType {

    APPLICATION("Application"),
    APP_LAYOUT("AppLayout"),
    APP_THEME("AppTheme"),
    APP_MENU("AppMenu"),
    APP_DICT("AppDict"),
    PAGE("Page"),
    PAGE_BLOCK("PageBlock"),
    PAGE_COMPONENT("PageComponent"),
    META_DATASOURCE("MetaDatasource"),
    METADATA("Metadata"),
    META_FIELD("MetaField"),
    META_RELATIONSHIP("MetaRelationship"),
    META_METHOD("MetaMethod"),
    META_VALIDATION("MetaValidation");

    private final String value;

    ProtocolType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}

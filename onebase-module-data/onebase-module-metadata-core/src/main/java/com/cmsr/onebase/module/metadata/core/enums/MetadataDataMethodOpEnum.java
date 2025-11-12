package com.cmsr.onebase.module.metadata.core.enums;

public enum MetadataDataMethodOpEnum {
    CREATE("创建数据"),
    UPDATE("更新数据"),
    DELETE("删除数据"),
    GET("查询数据"),
    GET_PAGE("分页查询数据"),
    GET_PAGE_OR("OR条件分页查询数据");

    private final String description;

    MetadataDataMethodOpEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

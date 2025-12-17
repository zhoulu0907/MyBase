package com.cmsr.onebase.framework.common.enums;

/**
 * @Author：huangjie
 * @Date：2025/11/23 17:47
 */
public enum VersionTagEnum {

    BUILD(0L, "编辑态"),

    RUNTIME(1L, "运行态");

    private Long value;
    private String description;

    VersionTagEnum(Long value, String description) {
        this.value = value;
        this.description = description;
    }

    public Long getValue() {
        return value;
    }
}

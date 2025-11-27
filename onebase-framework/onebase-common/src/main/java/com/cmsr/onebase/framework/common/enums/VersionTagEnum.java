package com.cmsr.onebase.framework.common.enums;

/**
 * @Author：huangjie
 * @Date：2025/11/23 17:47
 */
public enum VersionTagEnum {

    BUILD(0, "编辑态"),

    RUNTIME(1, "运行态");

    private Integer value;
    private String description;

    VersionTagEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

}

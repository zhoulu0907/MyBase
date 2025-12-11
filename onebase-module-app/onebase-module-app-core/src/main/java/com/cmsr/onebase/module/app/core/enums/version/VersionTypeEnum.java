package com.cmsr.onebase.module.app.core.enums.version;


/**
 * @Author：huangjie
 * @Date：2025/8/12 11:20
 */
public enum VersionTypeEnum {

    RUNTIME(1),
    HISTORY(2),
    ;

    private final int value;

    VersionTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

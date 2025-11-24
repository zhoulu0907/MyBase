package com.cmsr.onebase.framework.common.enums;

/**
 * @Author：huangjie
 * @Date：2025/11/23 17:47
 */
public enum VersionStatusEnum {

    RUNTIME("run", "运行态"),
    BUILD("bld ", "编辑态"),
    HISTORY("his ", "历史态");

    private String code;
    private String description;

    VersionStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

}

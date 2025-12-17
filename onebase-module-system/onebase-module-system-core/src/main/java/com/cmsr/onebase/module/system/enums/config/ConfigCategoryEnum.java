package com.cmsr.onebase.module.system.enums.config;

import lombok.Getter;

/**
 * 角色标识枚举
 */
@Getter
public enum ConfigCategoryEnum {

    TENANT("tenant", "空间"),
    CORP("corp", "企业"),
    GLOBAL("global", "全局"),
    ;

    ConfigCategoryEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 角色编码
     */
    private final String code;
    /**
     * 名字
     */
    private final String name;




}

package com.cmsr.onebase.module.system.enums.config;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 角色标识枚举
 */
@Getter
public enum ConfigCategoryEnum  implements ArrayValuable<String> {

    TENANT("tenant", "空间"),
    CORP("corp", "企业"),
    APP("app", "应用"),
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

    public static final String[] ARRAYS = Arrays.stream(values()).map(ConfigCategoryEnum::getCode).toArray(String[]::new);


    @Override
    public String[] array() {
        return ARRAYS;
    }
}

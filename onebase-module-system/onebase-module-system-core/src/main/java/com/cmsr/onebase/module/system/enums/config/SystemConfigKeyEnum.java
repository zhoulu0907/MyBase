package com.cmsr.onebase.module.system.enums.config;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.Getter;

import java.util.Arrays;

/**
 * 角色标识枚举
 */
@Getter
public enum SystemConfigKeyEnum implements ArrayValuable<String> {

    ThirdUserConfig("thirdUserConfig ", "外部用户管理配置项"),
    SaasModeConfig("saasModeConfig", "SaaS模式配置项");

    SystemConfigKeyEnum(String code, String name) {
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

    public static final String[] ARRAYS = Arrays.stream(values()).map(SystemConfigKeyEnum::getCode).toArray(String[]::new);


    @Override
    public String[] array() {
        return ARRAYS;
    }
}

package com.cmsr.onebase.module.system.enums.login;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum LongTypeEnum implements ArrayValuable<String> {
    /**
     * 密码登录
     */
    PASSWORD ("password", "密码"),
    /**
     * 验证码登录
     */
    VERIFYCODE("verifycode", "验证码");

    public static final String[] ARRAYS = Arrays.stream(values()).map(LongTypeEnum::getCode).toArray(String[]::new);

    /**
     * 类型代码
     */
    private final String code;
    /**
     * 类型名称
     */
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

}

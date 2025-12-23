package com.cmsr.onebase.module.system.enums.login;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum LoginSourceEnum implements ArrayValuable<String> {
    /**
     * app账密登录
     */
    APPLOGIN ("app-login", "账密登录"),
    /**
     * app手机登录
     */
    APPLOGINMOBILE("app-login-mobile", "手机登录"),

    /**
     * 企业手机登录
     */
    CORPLOGIN("corp-login", "企业手机登录"),

    /**
     * 三方用户手机登录
     */
    THIRDUSERLOGIN("third-login", "三方用户登录");


    public static final String[] ARRAYS = Arrays.stream(values()).map(LoginSourceEnum::getCode).toArray(String[]::new);

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

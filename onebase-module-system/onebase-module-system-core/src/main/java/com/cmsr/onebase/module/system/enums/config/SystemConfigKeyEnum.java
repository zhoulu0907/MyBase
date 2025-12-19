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
    SaasModeConfig("saasModeConfig", "SaaS模式配置项"),
    appThirdUserEnable("appThirdUserEnable", "应用三方用户登录-开关"),
    appThirdUserRegisterShow("appThirdUserRegisterShow", "应用三方用户登录-注册入口显隐"),
    appThirdUserForgetPwdShow("appThirdUserForgetPwdShow", "应用三方用户登录-忘记密码显隐"),
    ;

    SystemConfigKeyEnum(String key, String name) {
        this.key = key;
        this.name = name;
    }

    //  默认值：应用三方用户登录-开关
    public static final boolean appThirdUserEnable_DefaultValue = true;
    // 应用三方用户登录-注册入口显隐
    public static final boolean appThirdUserRegisterShow_DefaultValue = true;
    // 应用三方用户登录-忘记密码显隐
    public static final boolean appThirdUserForgetPwdShow_DefaultValue = true;

    /**
     * 角色编码
     */
    private final String key;

    /**
     * 名字
     */
    private final String name;

    public static final String[] ARRAYS = Arrays.stream(values()).map(SystemConfigKeyEnum::getKey).toArray(String[]::new);


    @Override
    public String[] array() {
        return ARRAYS;
    }

    public static SystemConfigKeyEnum getByKey(String key) {
        for (SystemConfigKeyEnum configKeyEnum : SystemConfigKeyEnum.values()) {
            if (configKeyEnum.getKey().equals(key)) {
                return configKeyEnum;
            }
        }
        return null;
    }
}

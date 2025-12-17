package com.cmsr.onebase.framework.common.enums;

import cn.hutool.core.util.ArrayUtil;
import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 函数类型分类枚举
 */
@AllArgsConstructor
@Getter
public enum SecurityCategoryCodeEnum implements ArrayValuable<String> {

    MULTI_DEVICE("MULTI_DEVICE", "多设备登录配置"),
    ANTI_BRUTE_FORCE("ANTI_BRUTE_FORCE", "防暴力破解配置"),
    OAUTH2("OAUTH2", "第三方登录配置"),
    PASSWORD_POLICY("PASSWORD_POLICY", "密码策略配置"),
    MFA("MFA", "多因子认证配置"),
    CAPTCHA_STRATEGY("CAPTCHA_STRATEGY", "验证码策略配置"),
    FILE_SECURITY("FILE_SECURITY", "文件安全配置"),
    DESENSITIZATION_SECURITY("DESENSITIZATION_SECURITY", "脱敏配置")
    ;

    public static final String[] ARRAYS = Arrays.stream(values()).map(SecurityCategoryCodeEnum::getValue).toArray(String[]::new);

    /**
     * 类型
     */
    private final String value;
    /**
     * 类型名
     */
    private final String name;

    public static SecurityCategoryCodeEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(codeEnum -> false, SecurityCategoryCodeEnum.values());
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

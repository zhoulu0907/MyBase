package com.cmsr.onebase.module.system.enums.catcha;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import com.cmsr.onebase.module.system.enums.tenant.SortEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SendTypeEnum  implements ArrayValuable<String> {
    /**
     * 邮箱
     */
    EMAIL("email", "邮箱"),
    /**
     * 手机
     */
    MOBILE("mobile", "手机");

    public static final String[] ARRAYS = Arrays.stream(values()).map(SendTypeEnum::getCode).toArray(String[]::new);

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

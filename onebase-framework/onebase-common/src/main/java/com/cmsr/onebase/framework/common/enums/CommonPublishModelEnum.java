package com.cmsr.onebase.framework.common.enums;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 通用发布模式枚举
 *
 */
@Getter
@AllArgsConstructor
public enum CommonPublishModelEnum implements ArrayValuable<String> {

    InnerModel("inner", "内部模式"),
    SaaSModel("saas", "saas模式");

    public static final String[] ARRAYS = Arrays.stream(values()).map(CommonPublishModelEnum::getValue).toArray(String[]::new);

    /**
     * 值
     */
    private final String value;
    /**
     * 名
     */
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

}

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
public enum CommonPublishModelEnum implements ArrayValuable<Integer> {

    InnerModel(0, "内部模式"),
    SaaSModel(1, "saas模式");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(CommonPublishModelEnum::getValue).toArray(Integer[]::new);

    /**
     * 值
     */
    private final Integer value;
    /**
     * 名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

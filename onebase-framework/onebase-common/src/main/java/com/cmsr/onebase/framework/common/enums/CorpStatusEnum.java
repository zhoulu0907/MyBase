package com.cmsr.onebase.framework.common.enums;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
@Getter
@AllArgsConstructor
public enum CorpStatusEnum implements ArrayValuable<Integer> {

    ENABLE(1, "启用"),
    DISABLE(0, "停用");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(CorpStatusEnum::getValue).toArray(Integer[]::new);

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

package com.cmsr.onebase.framework.common.enums;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CorpAppReationStatusEnum implements ArrayValuable<Integer> {
    ALL(0, "全部"),
    ENABLE(1, "已启用"),
    DISABLE(2, "已禁用"),
    EXPIRES(3, "已过期");


    public static final Integer[] ARRAYS = Arrays.stream(values()).map(CorpAppReationStatusEnum::getValue).toArray(Integer[]::new);

    /**
     * 值
     */
    private final int value;
    /**
     * 名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}

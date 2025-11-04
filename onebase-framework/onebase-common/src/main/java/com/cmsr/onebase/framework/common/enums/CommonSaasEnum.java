package com.cmsr.onebase.framework.common.enums;

import cn.hutool.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
@Getter
@AllArgsConstructor
public enum CommonSaasEnum implements ArrayValuable<Integer> {

    ENABLE(1, "开启"),
    DISABLE(0, "关闭");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(CommonSaasEnum::getValue).toArray(Integer[]::new);

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

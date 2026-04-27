package com.cmsr.onebase.framework.common.enums;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 全局用户类型枚举
 */
@AllArgsConstructor
@Getter
public enum ReturnTypeEnum implements ArrayValuable<String> {

    TEXT("TEXT", "文本"),
    NUMBER("NUMBER", "数字"),
    BOOL("BOOL", "布尔"),
    ;

    public static final String[] ARRAYS = Arrays.stream(values()).map(ReturnTypeEnum::getValue).toArray(String[]::new);

    /**
     * 类型
     */
    private final String value;
    /**
     * 类型名
     */
    private final String name;

    public static ReturnTypeEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value), ReturnTypeEnum.values());
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

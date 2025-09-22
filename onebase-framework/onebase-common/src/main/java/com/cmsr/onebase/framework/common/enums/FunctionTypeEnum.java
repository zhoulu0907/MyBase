package com.cmsr.onebase.framework.common.enums;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import com.cmsr.onebase.framework.common.tools.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 全局用户类型枚举
 */
@AllArgsConstructor
@Getter
public enum FunctionTypeEnum implements ArrayValuable<String> {

    TEXT("TEXT", "文本函数"),
    NUMBER("NUMBER", "数字函数"),
    DATE("DATE", "日期函数"),
    USER("USER", "人员函数"),
    LOGIC("LOGIC", "逻辑函数"),
    ;

    public static final String[] ARRAYS = Arrays.stream(values()).map(FunctionTypeEnum::getValue).toArray(String[]::new);

    /**
     * 类型
     */
    private final String value;
    /**
     * 类型名
     */
    private final String name;

    public static FunctionTypeEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value), FunctionTypeEnum.values());
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

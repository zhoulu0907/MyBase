package com.cmsr.onebase.framework.common.enums;

import cn.hutool.core.util.ArrayUtil;
import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 运行模式枚举
 */
@AllArgsConstructor
@Getter
public enum RunModeEnum implements ArrayValuable<String> {

    PLATFORM("platform", "平台端"),
    BUILD("build", "编辑态"),
    RUNTIME("runtime", "运行态"),
    ;

    public static final String[] ARRAYS = Arrays.stream(values()).map(RunModeEnum::getValue).toArray(String[]::new);

    /**
     * 类型
     */
    private final String value;
    /**
     * 类型名
     */
    private final String name;

    public static RunModeEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value), RunModeEnum.values());
    }

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

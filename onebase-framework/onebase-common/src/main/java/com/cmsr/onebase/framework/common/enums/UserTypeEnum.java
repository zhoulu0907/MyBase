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
public enum UserTypeEnum implements ArrayValuable<Integer> {

    PLATFORM(0, "平台"), // 面向 平台管理，普通用户
    RUNTIME(1, "会员"), // 面向 运行态，普通用户
    BUILD(2, "管理员"); // 面向 编辑态，管理后台

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(UserTypeEnum::getValue).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer value;
    /**
     * 类型名
     */
    private final String name;

    public static UserTypeEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(userType -> userType.getValue().equals(value), UserTypeEnum.values());
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}

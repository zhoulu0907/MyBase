package com.cmsr.onebase.module.system.enums.user;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum CreateSourceEnum implements ArrayValuable<String> {
    /**
     * 后台注册
     */
    BACK ("back", "后台注册"),
    /**
     * 自主注册
     */
    SELF("self", "自主注册");

    public static final String[] ARRAYS = Arrays.stream(values()).map(CreateSourceEnum::getCode).toArray(String[]::new);

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

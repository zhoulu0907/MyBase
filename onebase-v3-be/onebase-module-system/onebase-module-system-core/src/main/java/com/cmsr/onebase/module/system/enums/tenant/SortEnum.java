package com.cmsr.onebase.module.system.enums.tenant;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import com.cmsr.onebase.framework.common.enums.CommonPublishModelEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 排序枚举值
 *
 * @author lingma
 * @date 2025-08-01
 */
@Getter
@AllArgsConstructor
public enum SortEnum implements ArrayValuable<String> {

    DESC("desc", "倒序"),
    ASC("asc", "正序");
    public static final String[] ARRAYS = Arrays.stream(values()).map(SortEnum::getValue).toArray(String[]::new);


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
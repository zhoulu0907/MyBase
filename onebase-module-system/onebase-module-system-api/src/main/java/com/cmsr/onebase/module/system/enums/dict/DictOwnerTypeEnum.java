package com.cmsr.onebase.module.system.enums.dict;

import cn.hutool.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 字典所有者类型枚举
 *
 * @author bty418
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum DictOwnerTypeEnum implements ArrayValuable<String> {
    /**
     * 全局字典
     */
    GLOBAL("global", "全局字典"),

    /**
     * 应用自定义字典
     */
    APP("app", "应用自定义字典"),

    /**
     * 租户公共字典（空间公共字典）
     */
    TENANT("tenant", "空间公共字典");

    public static final String[] ARRAYS = Arrays.stream(values()).map(DictOwnerTypeEnum::getType).toArray(String[]::new);

    /**
     * 类型值
     */
    private final String type;

    /**
     * 类型名称
     */
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 判断是否为应用类型字典
     *
     * @param type 字典所有者类型
     * @return 是否为应用类型
     */
    public static boolean isApp(String type) {
        return ObjUtil.equal(APP.type, type);
    }

    /**
     * 判断是否为租户类型字典
     *
     * @param type 字典所有者类型
     * @return 是否为租户类型
     */
    public static boolean isTenant(String type) {
        return ObjUtil.equal(TENANT.type, type);
    }

}





package com.cmsr.onebase.module.system.enums.app;

import cn.hutool.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 对接外部平台类型枚举
 *
 * @author bty418
 * @date 2025-10-21
 */
@Getter
@AllArgsConstructor
public enum PlatformTypeEnum implements ArrayValuable<String> {

    /**
     * 天工平台
     */
    TIANGONG("tiangong", "天工平台"),

    /**
     * 灵畿平台
     */
    LINGJI("lingji", "灵畿平台");

    public static final String[] ARRAYS = Arrays.stream(values()).map(PlatformTypeEnum::getType).toArray(String[]::new);

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
    public static boolean isTiangong(String type) {
        return ObjUtil.equal(TIANGONG.type, type);
    }

    /**
     * 判断是否为租户类型字典
     *
     * @param type 字典所有者类型
     * @return 是否为租户类型
     */
    public static boolean isLingJi(String type) {
        return ObjUtil.equal(LINGJI.type, type);
    }


}





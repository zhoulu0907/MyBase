package com.cmsr.onebase.module.system.enums.project;

import cn.hutool.core.util.ObjUtil;
import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 项目来源枚举
 *
 * @author claude
 * @date 2026-03-23
 */
@Getter
@AllArgsConstructor
public enum ProjectSourceEnum implements ArrayValuable<String> {

    /**
     * 灵畿平台
     */
    LINGJI("lingji", "灵畿平台"),

    /**
     * 天工平台
     */
    TIANGONG("tiangong", "天工平台"),

    /**
     * 系统内部创建
     */
    INTERNAL("internal", "系统内部创建");

    public static final String[] ARRAYS = Arrays.stream(values()).map(ProjectSourceEnum::getSource).toArray(String[]::new);

    /**
     * 来源值
     */
    private final String source;

    /**
     * 来源名称
     */
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    /**
     * 判断是否为灵畿来源
     *
     * @param source 来源类型
     * @return 是否为灵畿来源
     */
    public static boolean isLingji(String source) {
        return ObjUtil.equal(LINGJI.source, source);
    }

    /**
     * 判断是否为内部创建
     *
     * @param source 来源类型
     * @return 是否为内部创建
     */
    public static boolean isInternal(String source) {
        return ObjUtil.equal(INTERNAL.source, source);
    }
}
package com.cmsr.onebase.module.infra.enums.file;

import com.cmsr.onebase.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 文件环境标识枚举
 *
 */
@AllArgsConstructor
@Getter
public enum FileVisitModeEnum implements ArrayValuable<String> {
    /**
     * 公开访问
     */
    PUBLIC("public","可公开下载，无需登录"),
    /**
     * 文件需登录鉴权
     */
    AUTHEN("authen","需校验登录信息后方可下载"),


    PERMISSION("permission","内部调用");

    /**
     * 值
     */
    private final String value;

    /**
     * 名
     */
    private final String name;

    public static final String[] ARRAYS = Arrays.stream(values()).map(FileVisitModeEnum::getValue).toArray(String[]::new);

    @Override
    public String[] array() {
        return ARRAYS;
    }
}

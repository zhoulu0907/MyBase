package com.cmsr.onebase.module.infra.api.file.enums;

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
     * 各runMode私有访问
     */
    PRIVATE("private","各runMode私有文件");

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

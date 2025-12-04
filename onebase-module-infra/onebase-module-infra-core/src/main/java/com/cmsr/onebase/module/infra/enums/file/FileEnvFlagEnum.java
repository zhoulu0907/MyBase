package com.cmsr.onebase.module.infra.enums.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件环境标识枚举
 *
 */
@AllArgsConstructor
@Getter
public enum FileEnvFlagEnum {
    /**
     * 公开访问
     */
    PUBLIC("public"),
    /**
     * 编辑态
     */
    BUILD("build"),
    /**
     * 运行态
     */
    RUNTIME("runtime"),
    /**
     * 平台端
     */
    PLATFORM("platform"),
    ;

    /**
     * 环境标识
     */
    private final String envFlag;
}

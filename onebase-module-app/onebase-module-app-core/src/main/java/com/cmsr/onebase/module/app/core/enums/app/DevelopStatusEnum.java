package com.cmsr.onebase.module.app.core.enums.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *  app开发状态
 */
@AllArgsConstructor
@Getter
public enum DevelopStatusEnum {
    /**
     * 迭代中
     */
    ITERATE("iterate", "迭代中")
    ;

    private final String code;

    private final String name;

}

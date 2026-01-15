package com.cmsr.onebase.module.dashboard.build.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TemplateTypeEnum {
    /**
     * 系统类型
     */
    SYSTEM_TYPE("system","系统模板"),
    /**
     * 应用类型
     */
    APP_TYPE("app","应用模板");

    /**
     * 值
     */
    private final String value;

    /**
     * 名
     */
    private final String name;

}

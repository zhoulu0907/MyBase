package com.cmsr.onebase.framework.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum XFromSceneTypeEnum {
    /**
     * 平台
     */
    PLATFORM("platform", "平台"),
    /**
     * 空间
     */
    TENANT("tenant", "空间"),
    /**
     * 企业
     */
    CORP("corp", "企业");
    /**
     * 类型代码
     */
    private final String code;
    /**
     * 类型名称
     */
    private final String name;
}

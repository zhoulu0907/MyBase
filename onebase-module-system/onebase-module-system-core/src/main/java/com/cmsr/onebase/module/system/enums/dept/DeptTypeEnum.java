package com.cmsr.onebase.module.system.enums.dept;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeptTypeEnum {
    /**
     * 空间
     */
    TENANT("tenant", "空间"),
    /**
     * 企业
     */
    CORP("corp", "企业"),

    /**
     * 三方用户
     */
    THIRD("third", "三方用户");

    /**
     * 类型代码
     */
    private final String code;
    /**
     * 类型名称
     */
    private final String name;
}

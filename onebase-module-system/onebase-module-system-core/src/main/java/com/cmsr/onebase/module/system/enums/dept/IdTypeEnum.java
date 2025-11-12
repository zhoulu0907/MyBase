package com.cmsr.onebase.module.system.enums.dept;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IdTypeEnum {
    /**
     * 用户
     */
    USER("user", "用户标识"),
    /**
     * 部门
     */
    DEPT("dept", "部门标识");

    /**
     * 类型代码
     */
    private final String code;
    /**
     * 类型名称
     */
    private final String name;
}

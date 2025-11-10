package com.cmsr.onebase.module.system.enums.dept;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IdTypeEnum {
    /**
     * 用户
     */
    USER(1, "用户标识"),
    /**
     * 部门
     */
    DEPT(2, "部门标识");

    /**
     * 类型代码
     */
    private final Integer code;
    /**
     * 类型名称
     */
    private final String name;
}

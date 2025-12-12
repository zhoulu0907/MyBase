package com.cmsr.onebase.module.system.enums.dept;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DefaultThirdDept {
    /**
     *  默认三方用户部门
     */
    DEFAULT_THIRD_DEPT("third", "dept_third_default", "外部协作用户");

    private final String value;

    private final String code;

    private final String name;


}

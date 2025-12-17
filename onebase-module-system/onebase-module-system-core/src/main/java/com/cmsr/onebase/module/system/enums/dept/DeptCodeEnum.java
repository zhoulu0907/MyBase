package com.cmsr.onebase.module.system.enums.dept;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DeptCodeEnum {
    /**
     * 默认三方用户部门
     */
    DEFAULT_THIRD_DEPT("dept_third_default", "外部协作部门"),
    ;

    private final String code;

    private final String name;


}

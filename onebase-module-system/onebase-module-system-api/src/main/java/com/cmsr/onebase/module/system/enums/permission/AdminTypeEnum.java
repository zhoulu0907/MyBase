package com.cmsr.onebase.module.system.enums.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminTypeEnum {
    /**
     * 内置用户
     */
    SYSTEM(1),
    /**
     * 自定义用户
     */
    CUSTOM(2);

    private final Integer type;
}

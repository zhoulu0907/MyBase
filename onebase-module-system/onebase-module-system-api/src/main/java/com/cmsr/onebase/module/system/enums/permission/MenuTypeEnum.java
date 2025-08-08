package com.cmsr.onebase.module.system.enums.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举类
 *
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum {

    Module(1), // 模块
    Menu(2), // 功能
    Action(3) // 操作
    ;

    /**
     * 类型
     */
    private final Integer type;

}

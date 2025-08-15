package com.cmsr.onebase.module.system.enums.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserPasswordEnum {

    /**
     * 密码
     */
    PASSWORD_ENUM("admin123");

    /**
     * 密码属性
     */
    private final String password;
}

package com.cmsr.onebase.module.app.enums.app;

/**
 * @Author：huangjie
 * @Date：2025/8/6 18:36
 */
public enum AuthRoleEnum {

    ROLE_ADMIN("ROLE_ADMIN", "管理员"),

    ROLE_USER("ROLE_USER", "普通用户");

    private String code;
    private String name;

    AuthRoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}

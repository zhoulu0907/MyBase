package com.cmsr.onebase.module.app.core.enums.auth;

/**
 * 角色类型枚举
 *
 * @Author：huangjie
 * @Date：2025/8/6 18:36
 */
public enum AuthRoleTypeEnum {

    /**
     * 系统管理员角色
     */
    SYSTEM_ADMIN(1, "ROLE_SYSTEM_ADMIN", "应用开发者"),

    /**
     * 系统普通用户角色
     */
    SYSTEM_USER(2, "ROLE_SYSTEM_USER", "普通用户"),

    /**
     * 用户自定义角色
     */
    CUSTOM_ROLE(3, null, "自定义角色");

    private final Integer value;

    private final String code;

    private final String name;

    AuthRoleTypeEnum(Integer value, String code, String name) {
        this.value = value;
        this.code = code;
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    /**
     * 判断角色类型是否允许添加和删除操作
     * 只有 CUSTOM_ROLE 可以添加和删除
     *
     * @param value 角色类型值
     * @return true-允许添加和删除，false-不允许添加和删除
     */
    public static boolean isSystemRoleType(Integer value) {
        return SYSTEM_ADMIN.getValue().equals(value) || SYSTEM_USER.getValue().equals(value);
    }

    public static boolean isSystemAdminRole(Integer value) {
        return SYSTEM_ADMIN.getValue().equals(value);
    }

}
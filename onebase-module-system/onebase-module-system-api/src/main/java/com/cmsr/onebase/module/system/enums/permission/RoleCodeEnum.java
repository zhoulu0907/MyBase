package com.cmsr.onebase.module.system.enums.permission;

import com.cmsr.onebase.framework.common.util.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色标识枚举
 */
@Getter
@AllArgsConstructor
public enum RoleCodeEnum {

    SUPER_ADMIN("super_admin", "平台管理员"),
    TENANT_ADMIN("tenant_admin", "租户管理员"),
    CORP_ADMIN("corp_admin", "企业管理员"),
    APP_DEVELOPER("app_developer", "应用开发者"),
    ;

    /**
     * 角色编码
     */
    private final String code;
    /**
     * 名字
     */
    private final String name;

    public static boolean isSuperAdmin(String code) {
        return ObjectUtils.equalsAny(code, SUPER_ADMIN.getCode());
    }


    public static boolean isTenantAdmin(String code) {
        return ObjectUtils.equalsAny(code, TENANT_ADMIN.getCode());
    }

}

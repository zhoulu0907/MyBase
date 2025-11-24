package com.cmsr.onebase.module.system.enums.permission;

import com.cmsr.onebase.framework.common.util.object.ObjectUtils;
import lombok.Getter;

/**
 * 角色标识枚举
 */
@Getter
public enum RoleCodeEnum {

    SUPER_ADMIN("super_admin", "平台管理员"),
    TENANT_ADMIN("tenant_admin", "租户管理员"),
    CORP_ADMIN("corp_admin", "企业管理员"),
    APP_DEVELOPER("app_developer", "应用开发者"),
    ;


    RoleCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 角色编码
     */
    private final String code;
    /**
     * 名字
     */
    private final String name;

    /**
     * 应用开发者权限编码列表(建议未来迁移到数据库配置)
     */
    private final String[] developerPermissionCodes = new String[]{
            "",
            "",
            ""
    };

    public static boolean isSuperAdmin(String code) {
        return ObjectUtils.equalsAny(code, SUPER_ADMIN.getCode());
    }


    public static boolean isTenantAdmin(String code) {
        return ObjectUtils.equalsAny(code, TENANT_ADMIN.getCode());
    }

    public static boolean isCorpAdmin(String code) {
        return ObjectUtils.equalsAny(code, CORP_ADMIN.getCode());
    }

}

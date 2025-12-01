package com.cmsr.onebase.module.system.enums.permission;

import com.cmsr.onebase.framework.common.util.object.ObjectUtils;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * 角色标识枚举
 */
@Getter
public enum RoleCodeEnum {

    SUPER_ADMIN("super_admin", "平台管理员"),
    TENANT_ADMIN("tenant_admin", "空间管理员"),
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
    private final Set<String> devloperPermissionCodes = new HashSet<String>() {{

        add("tenant"); // 一级
        add("tenant:app"); // 应用管理
        add("tenant:app:create"); // 应用新增
        add("tenant:app:delete"); // 应用删除
        add("tenant:app:enable");  // 应用禁用
        add("tenant:app:query");  //应用查看
        add("tenant:app:update"); // 应用修改

        add("tenant:profile"); // 个人中心
        add("tenant:profile:query");  // 个人中心查看
        add("tenant:profile:update");  // 个人中心修改
        add("tenant:profile:reset-pwd"); //修改密码

        add("tenant:corp"); // 空间信息查看
        add("tenant:corp:query"); // 空间信息查看

        add("tenant:user"); // 用户查看
        add("tenant:user:query"); // 用户查看

        add("tenant:role");  // 角色查看
        add("tenant:role:query");  // 角色查看

        add("tenant:dept");  //组织查看
        add("tenant:dept:query");  //组织查看

        add("tenant:space"); // 空间信息查看
        add("tenant:space:query"); // 空间信息查看

        add("tenant:dict"); // 数据字典查看
        add("tenant:dict:query"); // 数据字典查看


    }};

    public static boolean isSuperAdmin(String code) {
        return ObjectUtils.equalsAny(code, SUPER_ADMIN.getCode());
    }


    public static boolean isTenantAdmin(String code) {
        return ObjectUtils.equalsAny(code, TENANT_ADMIN.getCode());
    }

    public static boolean isCorpAdmin(String code) {
        return ObjectUtils.equalsAny(code, CORP_ADMIN.getCode());
    }

    public static boolean isDevloperAdmin(String code) {
        return ObjectUtils.equalsAny(code, APP_DEVELOPER.getCode());
    }
}

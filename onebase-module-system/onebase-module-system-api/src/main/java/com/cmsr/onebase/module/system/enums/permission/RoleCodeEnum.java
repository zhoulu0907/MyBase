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


    /**
     * 应用开发者权限编码列表(建议未来迁移到数据库配置)
     */
    public static final Set<String> devloperPermissionCodes = new HashSet<>() {{

        add("tenant"); // 一级
        add("tenant:app"); // 应用管理
        add("tenant:app:create"); // 应用新增
        add("tenant:app:delete"); // 应用删除
        add("tenant:app:enable");  // 应用禁用
        add("tenant:app:query");  //应用查看
        add("tenant:app:update"); // 应用修改system/user/

        add("tenant:profile"); // 个人中心
        add("tenant:profile:query");  // 个人中心查看
        add("tenant:profile:update");  // 个人中心修改
        add("tenant:profile:reset-pwd"); //修改密码

     //   add("tenant:corp"); // 企业查询列表
        add("tenant:corp:query"); // 个人中心-我创建的企业 使用查询

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

        add("tenant:info"); // 空间信息
        add("tenant:info:query"); // 空间信息查看

    }};

    /**
     * 空间全部用户的默认权限(建议未来迁移到数据库配置)
     */
    public static final Set<String> tenantDefaultPermissionCodes = new HashSet<>() {{

        add("tenant"); // 一级
        add("tenant:app"); // 应用管理
        add("tenant:app:query");  //应用查看

        add("tenant:profile"); // 个人中心
        add("tenant:profile:query");  // 个人中心查看
        add("tenant:profile:update");  // 个人中心修改
        add("tenant:profile:reset-pwd"); //修改密码

        add("tenant:corp:query"); // 个人中心-我创建的企业 使用查询

        add("tenant:user"); // 用户查看
        add("tenant:user:query"); // 用户查看

        add("tenant:role");  // 角色查看
        add("tenant:role:query");  // 角色查看

        add("tenant:dept");  //组织查看
        add("tenant:dept:query");  //组织查看

        add("tenant:space"); // 空间信息查看
        add("tenant:space:query"); // 空间信息查看

        add("tenant:info"); // 空间信息
        add("tenant:info:query"); // 空间信息查看

    }};

    /**
     * 企业全部用户的默认权限(建议未来迁移到数据库配置)
     */
    public static final Set<String> corpDefaultPermissionCodes = new HashSet<>() {{
        add("tenant"); // 一级
        add("tenant:app"); // 应用管理
        add("tenant:app:query");  //应用查看

        add("corp"); // 一级

        add("corp:user"); // 二级 用户
        add("corp:user:query"); // 三级 用户查看

        add("corp:dept"); // 二级 部门
        add("corp:dept:query"); // 三级 部门查看

        add("corp:profile"); // 个人中心
        add("corp:profile:query");  // 个人中心查看
        add("corp:profile:update");  // 个人中心修改
        add("corp:profile:reset-pwd"); //修改密码

        add("corp:info"); // 二级 企业中心
        add("corp:info:query"); // 三级 企业查看

    }};


    /**
     * 全部用户的默认权限(建议未来迁移到数据库配置)
     */
    public static final Set<String> globalDefaultPermissionCodes = new HashSet<>() {{
        add("tenant"); // 一级
        add("tenant:app"); // 应用管理
        add("tenant:app:query");  //应用查看
    }};

}

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
    private final Set<Long> devloperPermissionCodes = new HashSet<Long>() {{
        add(544485027803697153L); // 应用新增
        add(544485027803697154L); // 应用删除
        add(544485027803697156L);  // 应用禁用
        add(544485027803697152L); //应用查看
        add(544485027803697155L); // 应用修改

        add(544483827918180352L); // 用户查看
        add(544479356609761280L);  // 角色查看
        add(544480773651173376L);  //组织查看

        add(544476963323121664L); // 空间信息查看
        add(544481822814375936L); // 数据字典查看
        add(544479208566661002L);  // 个人中心查看
        add(544479208566661003L);  // 个人中心修改
        add(544479208566661004L); //修改密码
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

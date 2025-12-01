package com.cmsr.onebase.module.system.enums;

/**
 * System 操作日志枚举
 * 目的：统一管理，也减少 Service 里各种“复杂”字符串
 */
public interface LogRecordConstants {

    String LOGIN_USER = "【{{#loginUser.info != null && #loginUser.info.['nickname'] !=null ? #loginUser.info.['nickname'] : #loginUser.id}}】";

    // ======================= SYSTEM_USER 用户 =======================

    String SYSTEM_USER_TYPE = "SYSTEM 用户";
    String SYSTEM_USER_CREATE_SUB_TYPE = "创建用户";
    String SYSTEM_USER_CREATE_SUCCESS = LOGIN_USER + "创建了用户【{{#user.nickname}}】";
    String SYSTEM_USER_UPDATE_SUB_TYPE = "更新用户";
    String SYSTEM_USER_UPDATE_SUCCESS = LOGIN_USER + "更新了用户【{{#oldUser.nickname}}】: {_DIFF{#updateReqVO}}";
    String SYSTEM_USER_DELETE_SUB_TYPE = "删除用户";
    String SYSTEM_USER_DELETE_SUCCESS = LOGIN_USER + "删除了用户【{{#user.nickname}}】";
    String SYSTEM_USER_UPDATE_PASSWORD_SUB_TYPE = "重置用户密码";
    String SYSTEM_USER_UPDATE_PASSWORD_SUCCESS = LOGIN_USER + "将用户的密码从【{{#user.password}}】重置为【{{#newPassword}}】";

    // ======================= SYSTEM_ROLE 角色 =======================

    String SYSTEM_ROLE_TYPE = "SYSTEM 角色";
    String SYSTEM_ROLE_CREATE_SUB_TYPE = "创建角色";
    String SYSTEM_ROLE_CREATE_SUCCESS = LOGIN_USER + "创建了角色【{{#role.name}}】";
    String SYSTEM_ROLE_UPDATE_SUB_TYPE = "更新角色";
    String SYSTEM_ROLE_UPDATE_SUCCESS = LOGIN_USER + "更新了角色【{{#role.name}}】: {_DIFF{#updateReqVO}}";
    String SYSTEM_ROLE_DELETE_SUB_TYPE = "删除角色";
    String SYSTEM_ROLE_DELETE_SUCCESS = LOGIN_USER + "删除了角色【{{#role.name}}】";

    // ======================= LOGIN_USER 登录 =======================

    String LOGIN_USER_TYPE = "LOGIN 用户";
    String LOGIN_USER_TENANT_SUB_TYPE = "空间用户登录";
    String LOGIN_USER_TENANT_SUCCESS = "空间用户【{{#user.nickname}}】登录";
    String LOGIN_USER_CORP_SUB_TYPE =  "企业用户登录";
    String LOGIN_USER_CORP_SUCCESS = "企业用户【{{#user.nickname}}】登录";
    String LOGIN_USER_PLATFORM_SUB_TYPE = "平台用户登录";
    String LOGIN_USER_PLATFORM_SUCCESS = "平台用户【{{#user.nickname}}】登录";
    String LOGIN_USER_APP_SUB_TYPE = "应用登录-内部模式";
    String LOGIN_USER_APP_SUCCESS = "应用登录-内部模式用户【{{#user.nickname}}】登录";
    String LOGIN_USER_SAAS_SUB_TYPE = "应用-Saas模式-登录";
    String LOGIN_USER_SAAS_SUCCESS = "应用-Saas模式用户【{{#user.nickname}}】登录";
    // ======================= LOGOUT 登出 =======================

    String LOGOUT_USER_SUB_TYPE = "用户登出";
    String LOGOUT_USER_SUCCESS = "用户【{{#user.nickname}}】登出";

    // ======================= PERMISSION 授权 =======================

    String SYSTEM_PERMISSION_TYPE = "ASSIGN_ROLE 授权";
    String SYSTEM_PERMISSION_ASSIGN_ROLE_MENU_SUB_TYPE = "赋予角色权限";
    String SYSTEM_PERMISSION_ASSIGN_ROLE_MENU_SUCCESS = LOGIN_USER +"赋予【{{#role.name}}】角色【{{#menuNames}}】权限";
    String SYSTEM_PERMISSION_ASSIGN_ROLE_DATA_SCOPE_SUB_TYPE = "赋予角色数据权限";
    String SYSTEM_PERMISSION_ASSIGN_ROLE_DATA_SCOPE_SUCCESS = LOGIN_USER +"赋予【{{#role.name}}】角色【{{#dataScopeDeptIds}}】数据权限";
    String SYSTEM_PERMISSION_ASSIGN_USER_ROLES_SUB_TYPE = "赋予用户角色";
    String SYSTEM_PERMISSION_ASSIGN_USER_ROLES_SUCCESS = LOGIN_USER +"给【{{#user.nickname}}】用户分配【{{#roleNames}}】角色";
    String SYSTEM_PERMISSIONSUB_ADD_ROLE_USERS_TYPE = "为角色分配用户";
    String SYSTEM_PERMISSION_ADD_ROLE_USERS__SUCCESS = LOGIN_USER +"为角色【{{#role.name}}】分配用户:【{{#userNames}}】";
    String SYSTEM_PERMISSION_DELETE_ROLE_USERS_SUB_TYPE = "从角色中移除用户";
    String SYSTEM_PERMISSION_DELETE_ROLE_USERS__SUCCESS = LOGIN_USER +"从角色【{{#role.name}}】中移除用户【{{#userNames}}】";
    String SYSTEM_PERMISSION_ADD_ROLE_MENUS_SUB_TYPE = "为角色分配菜单&权限";
    String SYSTEM_PERMISSION_ADD_ROLE_MENUS_SUCCESS = LOGIN_USER +"为角色【{{#role.name}}】分配菜单&权限【{{#menuNames}}】";
    String SYSTEM_PERMISSION_DELETE_ROLE_MENUS_SUB_TYPE = "从角色中移除菜单&权限";
    String SYSTEM_PERMISSION_DELETE_ROLE_MENUS_SUCCESS = LOGIN_USER +"从角色【{{#role.name}}】中移除菜单&权限【{{#menuNames}}】";

    // ======================= LICENSE 变更 =======================

    String SYSTEM_LICENSE_TYPE = "LICENSE 变更";
    String SYSTEM_LICENSE_IMPORT_SUB_TYPE = "导入凭证";
    String SYSTEM_LICENSE_IMPORT_SUCCESS = LOGIN_USER + "导入凭证【{{#licenseId}}】";

    // ======================= TENANT 租户 =======================

    String SYSTEM_TENANT_TYPE = "SYSTEM 租户";
    String SYSTEM_TENANT_CREATE_SUB_TYPE = "创建租户";
    String SYSTEM_TENANT_CREATE_SUCCESS = LOGIN_USER +"创建了租户【{{#tenant.name}}】";
    String SYSTEM_TENANT_UPDATE_SUB_TYPE = "更新租户";
    String SYSTEM_TENANT_UPDATE_SUCCESS = LOGIN_USER +"更新了租户【{{#tenant.name}}】";
    String SYSTEM_TENANT_DELETE_SUB_TYPE = "删除租户";
    String SYSTEM_TENANT_DELETE_SUCCESS = LOGIN_USER +"删除了租户【{{#tenant.name}}】";

    // ======================= OAUTH 授权=======================

    String SYSTEM_OAUTH_TYPE = "OAUTH 授权";
    String SYSTEM_OAUTH_APPROVE_SUB_TYPE = "申请授权";
    String SYSTEM_OAUTH_APPROVE_SUCCESS = LOGIN_USER + "授权【{{#client.name}}】成功";

    // ======================= MENU 菜单 =======================

    String SYSTEM_MENU_TYPE = "MENU 菜单";
    String SYSTEM_MENU_CREATE_SUB_TYPE = "创建菜单";
    String SYSTEM_MENU_CREATE_SUCCESS = LOGIN_USER +"创建【{{#menu.name}}】菜单成功";
    String SYSTEM_MENU_UPDATE_SUB_TYPE = "更新菜单";
    String SYSTEM_MENU_UPDATE_SUCCESS = LOGIN_USER +"用户更新了菜单 【{{#menu.name}}】";
    String SYSTEM_MENU_DELETE_SUB_TYPE = "删除菜单";
    String SYSTEM_MENU_DELETE_SUCCESS = LOGIN_USER +"删除了用户【{{#menu.name}}】";

    // ======================= CORP 企业 =======================

    String SYSTEM_CORP_TYPE = "CORP 企业";
    String SYSTEM_CORP_CREATE_SUB_TYPE = "创建企业";
    String SYSTEM_CORP_CREATE_SUCCESS = LOGIN_USER + "创建【{{#corpName}}】企业成功";
    String SYSTEM_CORP_UPDATE_SUB_TYPE = "更新企业";
    String SYSTEM_CORP_UPDATE_SUCCESS = LOGIN_USER + "用户更新了企业 【{{#corp.corpName}}】";
    String SYSTEM_CORP_DELETE_SUB_TYPE = "删除企业";
    String SYSTEM_CORP_DELETE_SUCCESS = LOGIN_USER + "删除了企业【{{#corp.corpName}}】";

    // ======================= DEPT 部门 =======================

    String SYSTEM_DEPT_TYPE = "DEPT 部门";
    String SYSTEM_DEPT_CREATE_SUB_TYPE = "创建部门";
    String SYSTEM_DEPT_CREATE_SUCCESS = LOGIN_USER +"创建【{{#dept.name}}】部门成功";
    String SYSTEM_DEPT_UPDATE_SUB_TYPE = "更新部门";
    String SYSTEM_DEPT_UPDATE_SUCCESS = LOGIN_USER +"用户更新了部门【{{#dept.name}}】";
    String SYSTEM_DEPT_DELETE_SUB_TYPE = "删除部门";
    String SYSTEM_DEPT_DELETE_SUCCESS = LOGIN_USER +"删除了部门【{{#dept.name}}】";

}

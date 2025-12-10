/**
 * 权限由三层组成：模块、功能、操作
 * 模块：租户管理、应用管理等
 * 功能：即各模块下的菜单，如租户管理模块下的用户管理、角色管理等菜单
 * 操作：主要为按钮，即菜单下的新增、编辑、删除、导入、导出等
 * 后端返回的操作权限标识格式：'模块:功能:操作'，如'system:user:create'
 * 本文件用于建立与后端权限标识对应的常量键名映射
 */

export const ALL_PERMISSION_CODE = '*:*:*';

export enum PERMISSION_TYPES {
  MODULE = 1,
  MENU = 2,
  ACTION = 3
}

/** 菜单-空间管理 */
export const TENANT_MENUS = {
  APP: 'tenant:app',//应用管理
  USER: 'tenant:user', //用户管理
  ROLE: 'tenant:role', //角色管理
  DEPT: 'tenant:dept', //组织管理
  DICT: 'tenant:dict', //数据字典管理
  SECURITY: 'tenant:security', //安全设置
  INFO: 'tenant:info',  //空间信息
  CORP: 'tenant:corp', //企业管理
  PROFILE: 'tenant:profile' //个人中心
};

/** 空间管理-用户管理 */
export const TENANT_USER_QUERY = 'tenant:user:query'; // 查询用户
export const TENANT_USER_CREATE = 'tenant:user:create'; // 增加用户
export const TENANT_USER_UPDATE = 'tenant:user:update'; // 修改用户
export const TENANT_USER_DELETE = 'tenant:user:delete'; // 删除用户
export const TENANT_USER_STATUS = 'tenant:user:enable'; // 启用/禁用用户
export const TENANT_USER_RESET = 'tenant:user:update-password'; // 重置密码
export const TENANT_USER_IMPORT = 'tenant:user:import'; // 数据导入 // todo
export const TENANT_USER_EXPORT = 'tenant:user:import'; // 数据导出 // todo
export const TENANT_USER_PERMISSION = {
  QUERY: TENANT_USER_QUERY,
  CREATE: TENANT_USER_CREATE,
  UPDATE: TENANT_USER_UPDATE,
  DELETE: TENANT_USER_DELETE,
  RESET: TENANT_USER_RESET,
  STATUS: TENANT_USER_STATUS,
  IMPORT: TENANT_USER_IMPORT,
  EXPORT: TENANT_USER_EXPORT
};

/** 租户管理-应用管理 */
export const TENANT_APP_QUERY = 'tenant:app:query'; // 查询应用
export const TENANT_APP_CREATE = 'tenant:app:create'; // 新增应用
export const TENANT_APP_UPDATE = 'tenant:app:update'; // 修改应用
export const TENANT_APP_DELETE = 'tenant:app:delete'; // 删除应用
export const TENANT_APP_PERMISSION = {
  QUERY: TENANT_APP_QUERY,
  CREATE: TENANT_APP_CREATE,
  UPDATE: TENANT_APP_UPDATE,
  DELETE: TENANT_APP_DELETE,
};

/** 租户管理-角色管理 */
export const TENANT_ROLE_QUERY = 'tenant:role:query'; // 查询角色
export const TENANT_ROLE_CREATE = 'tenant:role:create'; // 新增角色
export const TENANT_ROLE_UPDATE = 'tenant:role:update'; // 修改角色
export const TENANT_ROLE_DELETE = 'tenant:role:delete'; // 删除角色
export const TENANT_ROLE_ASSIGN_USER = 'tenant:role:user'; // 分配用户
export const TENANT_ROLE_ASSIGN_PERMISSION = 'tenant:role:permission'; // 分配角色权限
export const TENANT_ROLE_PERMISSION = {
  QUERY: TENANT_ROLE_QUERY,
  CREATE: TENANT_ROLE_CREATE,
  UPDATE: TENANT_ROLE_UPDATE,
  DELETE: TENANT_ROLE_DELETE,
  USER: TENANT_ROLE_ASSIGN_USER,
  PERMISSION: TENANT_ROLE_ASSIGN_PERMISSION
};

/** 租户管理-组织管理 */
export const TENANT_DEPT_QUERY = 'tenant:dept:query';
export const TENANT_DEPT_CREATE = 'tenant:dept:create';
export const TENANT_DEPT_SUB_DEPT = 'tenant:dept:update'; // todo 添加子部门 待确认
export const TENANT_DEPT_UPDATE = 'tenant:dept:update';
export const TENANT_DEPT_DELETE = 'tenant:dept:delete';
export const TENANT_DEPT_PERMISSION = {
  QUERY: TENANT_DEPT_QUERY,
  CREATE: TENANT_DEPT_CREATE,
  SUB_DEPT: TENANT_DEPT_SUB_DEPT,
  UPDATE: TENANT_DEPT_UPDATE,
  DELETE: TENANT_DEPT_DELETE
};

/** 租户管理-数据字典管理 */
export const TENANT_DICT_QUERY = 'tenant:dict:query';
export const TENANT_DICT_CREATE = 'tenant:dict:create';
export const TENANT_DICT_UPDATE = 'tenant:dict:update';
export const TENANT_DICT_DELETE = 'tenant:dict:delete';
export const TENANT_DICT_STATUS = 'tenant:dict:enable';
export const TENANT_DICT_PERMISSION = {
  QUERY: TENANT_DICT_QUERY,
  CREATE: TENANT_DICT_CREATE,
  UPDATE: TENANT_DICT_UPDATE,
  DELETE: TENANT_DICT_DELETE,
  STATUS: TENANT_DICT_STATUS
};

/** 租户管理-空间信息 */
export const TENANT_INFO_QUERY = 'tenant:info:query';
export const TENANT_INFO_UPDATE = 'tenant:info:update';
export const TENANT_INFO_PERMISSION = {
  QUERY: TENANT_INFO_QUERY,
  UPDATE: TENANT_INFO_UPDATE
};

/**租户管理-租户个人信息 */
export const TENANT_PROFILE_QUERY = 'tenant:profile:query';
export const TENANT_PROFILE_UPDATE = 'tenant:profile:update';
export const TENANT_PROFILE_RESETPWD = 'tenant:profile:reset-pwd';
export const TENANT_PROFILE_PERMISSION = {
  QUERY: TENANT_PROFILE_QUERY,
  UPDATE: TENANT_PROFILE_UPDATE,
  RESETPWD: TENANT_PROFILE_RESETPWD
};

/** 数据权限-权限范围 */
export const PERMISSION_SCOPE: Record<string, string> = {
  allData: '全部数据',
  ownSubmit: '本人提交',
  departmentSubmit: '本部门提交',
  subDepartmentSubmit: '下级部门提交',
  customCondition: '自定义条件'
};

/** 数据权限-操作权限 */
export const OPERATION_OPTIONS: Record<string, string> = {
  edit: '编辑',
  delete: '删除'
};

/****************************
 *
 *
 * 以下为运行态权限
 *
 *
 *
 ***************************/

export enum PUBLISH_MODULE {
  SASS = 'saas',
  INNER = 'inner'
}

/** 菜单-企业管理 */
export const CORP_MENUS = {
  USER: 'corp:user', //用户
  DEPT: 'corp:dept',   //组织管理
  PROFILE: 'corp:profile', //个人中心
  AUTHORIZED: 'corp:app-auth', //授权应用
  CORP_INFO: 'corp:info' //企业信息
};

/** 企业管理-用户管理 */
export const CORP_USER_QUERY = 'corp:user:query'; // 查询用户
export const CORP_USER_CREATE = 'corp:user:create'; // 增加用户
export const CORP_USER_UPDATE = 'corp:user:update'; // 修改用户
export const CORP_USER_DELETE = 'corp:user:delete'; // 删除用户
export const CORP_USER_STATUS = 'corp:user:enable'; // 启用/禁用用户
export const CORP_USER_RESET = 'corp:user:reset-pwd'; // 重置密码
export const CORP_USER_IMPORT = 'corp:user:import'; // 数据导入 // todo
export const CORP_USER_EXPORT = 'corp:user:import'; // 数据导出 // todo
export const CORP_USER_PERMISSION = {
  QUERY: CORP_USER_QUERY,
  CREATE: CORP_USER_CREATE,
  UPDATE: CORP_USER_UPDATE,
  DELETE: CORP_USER_DELETE,
  RESET: CORP_USER_RESET,
  STATUS: CORP_USER_STATUS,
  IMPORT: CORP_USER_IMPORT,
  EXPORT: CORP_USER_EXPORT
};

/** 企业管理-组织管理 */
export const CORP_DEPT_QUERY = 'corp:dept:query';
export const CORP_DEPT_CREATE = 'corp:dept:create';
export const CORP_DEPT_UPDATE = 'corp:dept:update';
export const CORP_DEPT_DELETE = 'corp:dept:delete';
export const CORP_DEPT_SUB_DEPT = 'corp:dept:update';
export const CORP_DEPT_PERMISSION = {
  QUERY: CORP_DEPT_QUERY,
  CREATE: CORP_DEPT_CREATE,
  UPDATE: CORP_DEPT_UPDATE,
  DELETE: CORP_DEPT_DELETE,
  SUB_DEPT: CORP_DEPT_SUB_DEPT
};

/** 企业管理-租户信息 */
export const CORP_INFO_QUERY = 'corp:info:query';
export const CORP_INFO_UPDATE = 'corp:info:update';
export const CORP_INFO_PERMISSION = {
  QUERY: CORP_INFO_QUERY,
  UPDATE: CORP_INFO_UPDATE
};

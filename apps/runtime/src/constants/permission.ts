/**
 * 权限由三层组成：模块、功能、操作
 * 模块：租户管理、应用管理等
 * 功能：即各模块下的菜单，如租户管理模块下的用户管理、角色管理等菜单
 * 操作：主要为按钮，即菜单下的新增、编辑、删除、导入、导出等
 * 后端返回的操作权限标识格式：'模块:功能:操作'，如'system:user:create'
 * 本文件用于建立与后端权限标识对应的常量键名映射
 */

export enum PERMISSION_TYPES {
  MODULE = 1,
  MENU = 2,
  ACTION = 3
}

export enum PUBLISH_MODULE {
  SASS = 'saas',
  INNER = 'inner'
}

/** 菜单-租户管理 */
export const TENANT_MENUS = {
  USER: 'tenant:user',
  ROLE: 'tenant:role',
  DEPT: 'tenant:dept',
  DICT: 'tenant:dict',
  INFO: 'tenant:info',
  AUTHORIZED: 'tenant:authorized',
  CORP_INFO:'tenant:corpInfo'
}

/** 租户管理-用户管理 */
export const TENANT_USER_QUERY = 'tenant:user:query'; // 查询用户
export const TENANT_USER_CREATE = 'tenant:user:create'; // 增加用户
export const TENANT_USER_UPDATE = 'tenant:user:update'; // 修改用户
export const TENANT_USER_DELETE = 'tenant:user:delete'; // 删除用户
export const TENANT_USER_STATUS = 'tenant:user:enable'; // 启用/禁用用户
export const TENANT_USER_RESET = 'tenant:user:reset'; // 重置密码
export const TENANT_USER_PERMISSION = {
  QUERY: TENANT_USER_QUERY,
  CREATE: TENANT_USER_CREATE,
  UPDATE: TENANT_USER_UPDATE,
  DELETE: TENANT_USER_DELETE,
  RESET: TENANT_USER_RESET,
  STATUS: TENANT_USER_STATUS
}

/** 租户管理-组织管理 */
export const TENANT_DEPT_QUERY = 'tenant:dept:query';
export const TENANT_DEPT_CREATE = 'tenant:dept:create';
export const TENANT_DEPT_UPDATE = 'tenant:dept:update';
export const TENANT_DEPT_DELETE = 'tenant:dept:delete';
export const TENANT_DEPT_SUB_DEPT = 'tenant:dept:update'; 
export const TENANT_DEPT_PERMISSION = {
  QUERY: TENANT_DEPT_QUERY,
  CREATE: TENANT_DEPT_CREATE,
  UPDATE: TENANT_DEPT_UPDATE,
  DELETE: TENANT_DEPT_DELETE,
  SUB_DEPT: TENANT_DEPT_SUB_DEPT
}

/** 租户管理-租户信息 */
export const TENANT_INFO_QUERY = 'tenant:info:query';
export const TENANT_INFO_UPDATE = 'tenant:info:update';
export const TENANT_INFO_PERMISSION = {
  QUERY: TENANT_INFO_QUERY,
  UPDATE: TENANT_INFO_UPDATE,
}
/** 应用管理-我的应用管理 */
export const APP_MYAPP_QUERY = 'app:app:query';
export const APP_MYAPP_CREATE = 'app:app:create';
export const APP_MYAPP_DELETE = 'app:app:delete';
export const APP_MYAPP_UPDATE = 'app:app:update';
export const APP_MYAPP_PERMISSION = {
  QUERY: APP_MYAPP_QUERY,
  CREATE: APP_MYAPP_CREATE,
  DELETE: APP_MYAPP_DELETE,
  UPDATE: APP_MYAPP_UPDATE,
}

export const ALL_PERMISSION_CODE = '*:*:*';

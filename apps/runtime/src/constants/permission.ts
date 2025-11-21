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
export const CORP_MENUS = {
  USER: 'corp:user',
  DEPT: 'corp:dept',
  PROFILE: 'corp:profile',
  AUTHORIZED: 'corp:app-auth',
  CORP_INFO:'corp:info'
}

/** 租户管理-用户管理 */
export const CORP_USER_QUERY = 'corp:user:query'; // 查询用户
export const CORP_USER_CREATE = 'corp:user:create'; // 增加用户
export const CORP_USER_UPDATE = 'corp:user:update'; // 修改用户
export const CORP_USER_DELETE = 'corp:user:delete'; // 删除用户
export const CORP_USER_STATUS = 'corp:user:enable'; // 启用/禁用用户
export const CORP_USER_RESET = 'corp:user:reset'; // 重置密码
export const CORP_USER_PERMISSION = {
  QUERY: CORP_USER_QUERY,
  CREATE: CORP_USER_CREATE,
  UPDATE: CORP_USER_UPDATE,
  DELETE: CORP_USER_DELETE,
  RESET: CORP_USER_RESET,
  STATUS: CORP_USER_STATUS
}

/** 租户管理-组织管理 */
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
}

/** 租户管理-租户信息 */
export const CORP_INFO_QUERY = 'corp:info:query';
export const CORP_INFO_UPDATE = 'corp:info:update';
export const CORP_INFO_PERMISSION = {
  QUERY: CORP_INFO_QUERY,
  UPDATE: CORP_INFO_UPDATE,
}
/** 应用管理-我的应用管理 */
export const APP_MYAPP_QUERY = 'corp:app:query';
export const APP_MYAPP_CREATE = 'corp:app:create';
export const APP_MYAPP_DELETE = 'corp:app:delete';
export const APP_MYAPP_UPDATE = 'corp:app:update';
export const APP_MYAPP_PERMISSION = {
  QUERY: APP_MYAPP_QUERY,
  CREATE: APP_MYAPP_CREATE,
  DELETE: APP_MYAPP_DELETE,
  UPDATE: APP_MYAPP_UPDATE,
}

export const ALL_PERMISSION_CODE = '*:*:*';

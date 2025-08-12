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


/** 模块 */
export const SYSTEM = 'system'; // 租户管理
export const APP = 'app'; // 应用管理 TODO：待确定

export const MODULE_MAP = {
  SYSTEM,
  APP
} as const;

/** 菜单-租户管理 */
export const USER = 'user'; // 用户管理
export const ROLE = 'role'; // 角色管理
export const DEPT = 'dept'; // 组织管理
export const DICT = 'dict'; // 字典管理
export const TENANT = 'tenant'; // 租户信息

/** 菜单-应用管理 */
// TODO: 待确定
export const MY_APP = ''; // 我的应用
export const APP_CENTER = ''; // 应用中心
export const APP_MALL = ''; // 商超中心
export const APP_HELPER = ''; // 帮助中心

export const MENU_MAP = {
  USER,
  ROLE,
  DEPT,
  DICT,
  TENANT,
  MY_APP,
  APP_CENTER,
  APP_MALL,
  APP_HELPER
} as const;

/** 操作 */
export const CREATE = 'create'; // 新增
export const DELETE = 'delete'; // 删除
export const EDIT = 'update'; // 修改
export const QUERY = 'query'; // 查询
export const IMPORT = 'import'; // 导入
export const EXPORT = 'export'; // 导出

export const ACTION_MAP = {
  CREATE,
  DELETE,
  EDIT,
  QUERY,
  IMPORT,
  EXPORT
} as const;

export const ALL_PERMISSION_CODE = '*:*:*';

export type ModuleName = keyof typeof MODULE_MAP;
export type MenuName = keyof typeof MENU_MAP;
export type ActionName = keyof typeof ACTION_MAP;

export type PermissionKey = `${ModuleName}:${MenuName}:${ActionName}`;
export type MenuKey = `${ModuleName}:${MenuName}`;
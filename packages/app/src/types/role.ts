// 角色管理

/**
 * 角色类型
 * 1: 管理员
 * 2: 普通用户
 * 3: 自定义用户
 */
type userType = 1 | 2 | 3;

/**
 * 角色状态常量
 * 1: 管理员
 * 2: 普通用户
 * 3: 自定义用户
 */
export const RoleType = {
  ADMIN: 1,
  USER: 2,
  CUSTOM: 3,
} as const;

export interface Role {
  /**
   * 角色ID
   */
  id: string;
  /**
   * 角色码
   */
  roleCode: string;
  /**
   * 角色名称
   */
  roleName: string;
  /**
   * 角色类型
   */
  roleType: userType;
}

export interface ListRoleReq {
  applicationId: string;
}

export interface CreateRoleReq {
  /**
   * 应用ID
   */
  applicationId: string;
  /**
   * 角色名称
   */
  roleName: string;
}

export interface RenameRoleReq {
  /**
   * 角色ID
   */
  id: string;
  /**
   * 角色名称
   */
  name: string;
}

export interface RoleAddUserReq {
  /**
   * 角色ID
   */
  roleId: string;
  /**
   * 用户ID列表
   */
  userIds: string[];
}

export interface RoleDeleteUserReq {
  /**
   * 角色ID
   */
  roleId: string;
  /**
   * 用户ID列表
   */
  userIds: string[];
}

export interface DeleteRoleReq {
  /**
   * 用户ID
   */
  roleId: string,
}

export interface GerRoleUserReq {
  /**
   * 页码，从 1 开始
   */
  pageNo: number;
  /**
   * 每页条数，最大值为 100
   */
  pageSize: number;
  /**
   * 角色ID
   */
  roleId: string;
}

export interface GetDeptUserReq {
  /**
   * 部门ID
   */
  deptId?: string;
  /**
   * 搜索关键词
   */
  keywords?: string;
  /**
   * 角色ID
   */
  roleId: string;
}
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

/**
 * AuthRoleAddDeptReq
 */
export interface RoleAddDeptReq {
    /**
     * 部门ID列表
     */
    deptIds: string[];
    /**
     * 是否包含子部门
     */
    isIncludeChild?: number;
    /**
     * 角色id
     */
    roleId: string;
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

/**
 * RoleDeleteMemberReq
 */
export interface RoleDeleteMemberReq {
    /**
     * 部门和成员列表
     */
    members: UserMembers[];
    /**
     * 角色id
     */
    roleId: string;
}

/**
 * UserMembers
 */
export interface UserMembers {
    id?: number;
    isIncludeChild?: number;
    memberId?: number;
    memberName?: string;
    memberType?: string;
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

export interface getRoleMembersReq {
  /**
   * 成员名称
   */
  memberName?: string;
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
  /**
   * 成员类型
   */
  memberType?: string;
}

/**
 * 返回数据
 *
 * DeptAndUsersRespDTO
 */
export interface DeptAndUsersRespDTO {
    /**
     * 当前部门信息
     */
    deptInfo?: DeptRespDTO;
    /**
     * 下级部门列表
     */
    deptList?: DeptRespDTO[];
    /**
     * 当前部门下直属用户列表
     */
    userList?: AdminUserRespDTO[];
}

/**
 * 当前部门信息
 *
 * DeptRespDTO
 *
 * com.cmsr.onebase.module.system.api.dept.dto.DeptRespDTO
 */
export interface DeptRespDTO {
    /**
     * 部门编号
     */
    id?: number;
    /**
     * 管理员的用户编号
     */
    leaderUserId?: number;
    /**
     * 部门名称
     */
    name?: string;
    /**
     * 父部门编号
     */
    parentId?: number;
    /**
     * 参见 CommonStatusEnum 枚举
     * 部门状态
     */
    status?: number;
}

/**
 * AdminUserRespDTO
 */
export interface AdminUserRespDTO {
    /**
     * 用户头像
     */
    avatar?: string;
    /**
     * 部门编号
     */
    deptId?: number;
    /**
     * 部门名称
     */
    deptName?: string;
    /**
     * 用户邮箱
     */
    email?: string;
    /**
     * 用户 ID
     */
    id?: number;
    /**
     * 手机号码
     */
    mobile?: string;
    /**
     * 用户昵称
     */
    nickname?: string;
    /**
     * 参见 CommonStatusEnum 枚举
     * 帐号状态
     */
    status?: number;
}

/**
 * 返回数据
 *
 * PageResultAuthRoleUsersPageRespVO
 */
export interface PageResultAuthRoleUsersPageRespVO {
    /**
     * 数据
     */
    list?: AuthRoleUsersPageRespVO[];
    /**
     * 总量
     */
    total?: number;
}

/**
 * AuthRoleUsersPageRespVO
 */
export interface AuthRoleUsersPageRespVO {
    /**
     * 部门名称
     */
    deptName?: string;
    /**
     * 用户邮箱
     */
    email?: string;
    /**
     * 用户ID
     */
    id?: number;
    /**
     * 手机号码
     */
    mobile?: string;
    /**
     * 用户名
     */
    nickname?: string;
    key?: string;
}
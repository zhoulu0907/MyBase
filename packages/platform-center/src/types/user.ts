import { SimpleRoleVO } from './role';
export interface UserVO {
  id: string;
  username: string;
  nickname: string;
  deptId: number;
  deptName: string;
  postIds: string[];
  email: string;
  mobile: string;
  sex: number;
  avatar: string;
  loginIp: string;
  status: number;
  remark: string;
  loginDate: Date;
  createTime: Date;
  roles?: SimpleRoleVO[];
  adminType?: UserType;
}

export enum UserType {
  SYSTEM = 1, // 系统预置用户
  CUSTOM = 2 // 自定义用户
}

/**
 * 返回数据
 *
 * UserProfileRespVO
 */
export interface UserProfileRespVO {
  /**
   * 用户头像
   */
  avatar?: string;
  /**
   * 创建时间
   */
  createTime?: string;
  /**
   * 所在部门
   */
  dept?: DeptSimpleRespVO;
  /**
   * 用户邮箱
   */
  email?: string;
  /**
   * 用户编号
   */
  id?: string;
  /**
   * 最后登录时间
   */
  loginDate?: string;
  /**
   * 最后登录 IP
   */
  loginIp?: string;
  /**
   * 手机号码
   */
  mobile?: string;
  /**
   * 用户昵称
   */
  nickname?: string;
  /**
   * 所属岗位数组
   */
  posts?: PostSimpleRespVO[];
  /**
   * 所属角色
   */
  roles?: RoleSimpleRespVO[];
  /**
   * 用户性别，参见 SexEnum 枚举类
   */
  sex?: number;
  /**
   * 用户账号
   */
  username?: string;
}

/**
 * 所在部门
 *
 * DeptSimpleRespVO
 */
export interface DeptSimpleRespVO {
  /**
   * 部门编号
   */
  id?: string;
  /**
   * 部门名称
   */
  name?: string;
  /**
   * 父部门 ID
   */
  parentId?: number;
}

/**
 * com.cmsr.onebase.module.system.vo.post.PostSimpleRespVO
 *
 * PostSimpleRespVO
 */
export interface PostSimpleRespVO {
  /**
   * 岗位序号
   */
  id?: string;
  /**
   * 岗位名称
   */
  name?: string;
}

/**
 * com.cmsr.onebase.module.system.vo.role.RoleSimpleRespVO
 *
 * RoleSimpleRespVO
 */
export interface RoleSimpleRespVO {
  /**
   * 角色编号
   */
  id?: string;
  /**
   * 角色名称
   */
  name?: string;
}

/**
 * UserProfileUpdateReqVO
 */
export interface UserProfileUpdateReq {
  /**
   * 角色头像
   */
  avatar?: string;
  /**
   * 用户邮箱
   */
  email?: string;
  /**
   * 手机号码
   */
  mobile?: string;
  /**
   * 用户昵称
   */
  nickname?: string;
  /**
   * 用户性别，参见 SexEnum 枚举类
   */
  sex?: number;
}

/**
 * UserProfileUpdatePasswordReqVO
 */
export interface UserProfileUpdatePwdReq {
  /**
   * 新密码
   */
  newPassword: string;
  /**
   * 旧密码
   */
  oldPassword: string;
}

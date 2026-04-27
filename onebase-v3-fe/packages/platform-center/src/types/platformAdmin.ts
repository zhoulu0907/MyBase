export enum PlatformAdminUserType {
  '系统默认账号'= 1,
  '普通账号',
}

/**
 * 新建管理员 cratePlatformAdminReq
 */
export interface cratePlatformAdminReq {
  username: string;
  nickname: string;
  password: string;
  mobile: string;
  email: string;
  // userType: PlatformAdminUserType;
  adminType: PlatformAdminUserType;
}
/**
 * 平台管理员信息
 */
export interface PlatformAdminInfo {
  id: string;
  avatar: string;
  /**
   * 创建时间
   */
  createTime: number;
  /**
   * 部门ID
   */
  deptId: number | null;
  /**
   * 部门名称
   */
  deptName: string | null;
  /**
   * 邮箱
   */
  email: string;
  /**
   * 登录IP
   */
  loginIp?: string;
  /**
   * 登录时间
   */
  loginTime: number;
  /**
   * 手机号码
   */
  mobile: string;
  /**
   * 昵称
   */
  nickname: string;
  /**
   * 岗位ID
   */
  postIds: number[] | null;
  /**
   * 角色ID
   */
  remark: string | null;
  /**
   * 性别
   */
  sex: number;
  /**
   * 状态
   */
  status: number;
  adminType: PlatformAdminUserType;
  /**
   * 用户类型
   */
  userType?: PlatformAdminUserType;
  /**
   * 用户名
   */
  username: string;
}
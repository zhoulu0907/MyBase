export interface DeptVO {
  id: number;
  name: string;
  parentId: number;
  status: number;
  sort: number;
  leaderUserId: number;
  phone: string;
  email: string;
  createTime: string;
  userCount: number;
  remark: string;
  [key: string]: any;
}

export interface GetDeptsByIdReq {
  id: string;
  idType: string;
}

export interface DeptTree extends DeptVO {
  userCount: number;
  children?: DeptVO[];
}

export type DeptForm = Partial<DeptVO>;

/**
 * UserAdminOrDirectorUpdateReqVO
 */
export interface UpdateAdminOrDirectorReq {
  /**
   * 部门Id
   */
  deptId: string;
  /**
   * 管理员类型
   */
  updateType: string;
  /**
   * 用户id
   */
  userId: string;
}

export interface GetDeptUserReq {
  /**
   * 部门Id
   */
  deptId?: string;
  /**
   * keywords
   */
  keywords?: string;
}
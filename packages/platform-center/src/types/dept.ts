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

export interface DeptListWithSearchReq {
  /**
   * 部门ID
   */
  deptId?: string;
  /**
   * 排除的roleIDs
   */
  excludeRoleIds?: string;
  /**
   * 排除的userIDs
   */
  excludeUserIds?: string;
  /**
   * 搜索关键词
   */
  keywords?: string;
}
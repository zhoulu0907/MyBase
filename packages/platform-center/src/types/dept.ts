export interface DeptVO {
  id: number
  name: string
  parentId: number
  status: number
  sort: number
  leaderUserId: number
  phone: string
  email: string
  createTime: Date
  userCount: number
  remark: string
}

export interface DeptTree extends DeptVO {
  userCount: number;
  children?: DeptVO[];
}
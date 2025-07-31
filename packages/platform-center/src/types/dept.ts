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
}
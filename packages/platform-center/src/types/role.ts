export interface RoleVO {
  id: number;
  name: string;
  code?: string;
  sort: number;
  status: number;
  type?: number;
  dataScope?: number;
  dataScopeDeptIds?: number[];
  createTime: Date;
  remark?: string;
  [key: string]: any;
}

export type RoleForm = Partial<RoleVO>;

export type UpdateStatusForm = Pick<RoleVO, 'id' | 'status'>;

export type SimpleRoleVO = Pick<RoleVO, 'id' | 'name'> & Partial<RoleVO>;

// 权限相关类型定义
export interface PermissionAction {
  id: number;
  name: string;
  type?: number;
}

export interface Permission {
  id: number;
  name: string;
  type?: number;
  remark?: string;
  children?: PermissionAction[];
}
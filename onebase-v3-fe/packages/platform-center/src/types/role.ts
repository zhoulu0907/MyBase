export interface RoleVO {
  id: string;
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
  id: string;
  name: string;
  type?: number;
  permission?: string;
}

export interface Permission {
  id: string;
  name: string;
  type?: number;
  remark?: string;
  parentId: string;
  permission?: string;
  children?: PermissionAction[];
}

export enum RoleType {
  SYSTEM = 1, // 系统内置角色
  CUSTOM = 2 // 自定义角色
}
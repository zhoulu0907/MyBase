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

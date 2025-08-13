// 应用服务

import {
  type ListRoleReq,
  type CreateRoleReq,
  type RoleAddUserReq,
  type RoleDeleteUserReq,
  type DeleteRoleReq,
} from '../types/role';
import { appService } from './clients';

//获取角色列表
export const listRole = (params: ListRoleReq) => {
  return appService.get(`/auth-role/list?applicationId=${params.applicationId}`);
};

// 新增角色
export const createRole = (params: CreateRoleReq) => {
  return appService.post('/auth-role/create', params);
};

// 角色添加成员
export const roleAddUser = (params: RoleAddUserReq) => {
  return appService.post('/auth-role/add-user', params);
};

// 角色删除成员
export const roleDeleteUser = (params: RoleDeleteUserReq) => {
  return appService.post('/auth-role/delete-user', params);
};

// 删除角色
export const deleteRole = (params: DeleteRoleReq) => {
  return appService.post(`/auth-role/delete?roleId=${params.roleId}`);
};

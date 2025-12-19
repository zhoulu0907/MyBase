// 应用服务

import { isRuntimeEnv } from '@onebase/common';
import {
  type CreateRoleReq,
  type DeleteRoleReq,
  type GerRoleUserReq,
  type GetDeptUserReq,
  getRoleMembersReq,
  type ListRoleReq,
  type RenameRoleReq,
  RoleAddDeptReq,
  type RoleAddUserReq,
  RoleDeleteMemberReq,
  type RoleDeleteUserReq
} from '../types/role';
import { appService, runtimeAppService } from './clients';

//获取角色列表
export const listRole = (params: ListRoleReq) => {
  return appService.get(`/auth-role/list?applicationId=${params.applicationId}`);
};

// 新增角色
export const createRole = (params: CreateRoleReq) => {
  return appService.post('/auth-role/create', params);
};

// 角色重命名
export const renameRole = (params: RenameRoleReq) => {
  const { id, name } = params;
  return appService.post(`/auth-role/rename?id=${id}&name=${name}`);
};

// 角色添加成员
export const roleAddUser = (params: RoleAddUserReq) => {
  return appService.post('/auth-role/add-user', params);
};

// 角色添加部门
export const roleAddDept = (params: RoleAddDeptReq) => {
  return appService.post('/auth-role/add-dept', params);
};

// 角色删除成员
export const roleDeleteUser = (params: RoleDeleteUserReq) => {
  return appService.post('/auth-role/delete-user', params);
};

// 角色删除成员
export const roleDeleteMember = (params: RoleDeleteMemberReq) => {
  return appService.post('/auth-role/delete-member', params);
};

// 删除角色
export const deleteRole = (params: DeleteRoleReq) => {
  return appService.post(`/auth-role/delete?roleId=${params.roleId}`);
};

// 获取角色用户列表
export const getRoleUser = (params: GerRoleUserReq) => {
  return appService.get('/auth-role/page-role-users', params);
};

// 获取部门用户列表
export const getDeptUser = (params: GetDeptUserReq, userType?: number) => {
  return (isRuntimeEnv() ? runtimeAppService : appService).get(`${userType ? `/auth-role/list-dept-users?userType=${userType}` : '/auth-role/list-dept-users'}`, params);
};

// 获取角色成员列表
export const getRoleMembers = (params: getRoleMembersReq) => {
  return appService.get('/auth-role/page-role-members', params);
};

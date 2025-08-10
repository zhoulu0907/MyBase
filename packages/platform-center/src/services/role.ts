import { type PageParam } from '../types/common';
import type { Permission, RoleForm, RoleVO, UpdateStatusForm } from '../types/role';
import { systemService } from './clients';

// 查询角色列表
export const getRolePage = (params: PageParam) => {
  return  systemService.get('/role/page', params);
};

// 查询角色（精简)列表
export const getSimpleRoleList = (): Promise<RoleVO[]> => {
  return systemService.get('/role/simple-list');
};

// 查询角色详情
export const getRole = (id: number) => {
  return systemService.get('/role/get?id=' + id);
};

// 新增角色
export const createRole = (data: RoleForm) => {
  return systemService.post('/role/create', data);
};

// 修改角色
export const updateRole = (data: RoleForm) => {
  return systemService.post('/role/update', data);
};

// 修改角色状态
export const updateRoleStatus = (data: UpdateStatusForm) => {
  return systemService.post('/role/update-status', data);
};

// 删除角色
export const deleteRole = (id: number) => {
  return systemService.post('/role/delete', { id });
};

// 批量删除角色
export const deleteRoleList = (ids: number[]) => {
  return systemService.post('/role/delete-list', { ids });
};

// 移除角色下的用户
export const removeRoleUsers = (roleId: number, userIds: number[]) => {
  return systemService.post('/permission/delete-role-users', { roleId, userIds });
};

// 为角色下增加用户
export const addRoleUsers = (roleId: number, userIds: number[]) => {
  return systemService.post('/permission/assign-role-users', { roleId, userIds });
};

// 移除某角色下的权限
export const removeRolePermission = (roleId: number, permissionId: number) => {
  return systemService.post('/role/remove-permission', {
    roleId,
    permissionId
  });
};

// 为某角色配置权限
export const configureRolePermissions = (roleId: number, permissions: Record<number, number[]>) => {
  return systemService.post('/permission/assign-role-menu', {
    roleId,
    permissions
  });
};

// 获取所有权限
export const getAllPermissions = (): Promise<Permission[]> => {
  return systemService.get('/menu/simple-list');
};

// 获取角色下已配置权限
export const getConfiguredPermissions = (roleId: number): Promise<any> => {
  return systemService.get('/permission/list-role-menus', { roleId });
};

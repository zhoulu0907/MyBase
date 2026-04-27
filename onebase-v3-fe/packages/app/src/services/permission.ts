// 角色权限管理

import {
  type GetPermissionReq,
  type UpdatePagePermissionReq,
  type UpdateOperationPermissionReq,
  type UpdateDataGroupPermissionReq,
  type UpdateFieldPermissionReq,
  UpdateViewPermissionReq,
} from '../types/permission';
import { appService } from './clients';

// 获取角色-菜单的功能权限
export const getFuncPermission = (params: GetPermissionReq) => {
  return appService.get('/auth-permission/get-function', params);
};

// 获取角色-菜单的数据权限
export const getDataPermission = (params: GetPermissionReq) => {
  return appService.get('/auth-permission/get-data', params);
};

// 获取角色-字段的权限
export const getFieldPermission = (params: GetPermissionReq) => {
  return appService.get('/auth-permission/get-field', params);
};

// 更新页面权限
export const updatePagePermission = (params: UpdatePagePermissionReq) => {
  return appService.post('/auth-permission/update-page-allowed', params);
};

// 更新操作权限
export const updateOperationPermission = (params: UpdateOperationPermissionReq) => {
  return appService.post('/auth-permission/update-operation', params);
};

// 更新视图权限
export const updateViewPermission = (params: UpdateViewPermissionReq) => {
  return appService.post('/auth-permission/update-view', params);
};

// 更新数据组权限
export const updateDataGroupPermission = (params: UpdateDataGroupPermissionReq) => {
  return appService.post('/auth-permission/update-data-group', params);
};

// 删除数据组权限
export const deleteDataGroup = (id: string) => {
  return appService.post(`/auth-permission/delete-data-group?id=${id}`);
};

// 更新字段权限
export const updateFieldPermission = (params: UpdateFieldPermissionReq) => {
  return appService.post('/auth-permission/update-field', params);
};

// 获取权限范围类型 /app/auth-permission/get-permission-scope
export const getScopeTypeApi = () => {
  return appService.get('/auth-permission/get-permission-scope');
};

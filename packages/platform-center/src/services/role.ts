import { type PageParam } from "../types/common";
import {
  type RoleVO,
  type RoleForm,
  type UpdateStatusForm,
} from "../types/role";
import systemClient from "./clients/system";

// 权限相关类型定义
export interface PermissionAction {
  id: number;
  name: string;
}

export interface Permission {
  id: number;
  name: string;
  type: string;
  remark?: string;
  actions?: PermissionAction[];
}

// 查询角色列表
export const getRolePage = async (params: PageParam) => {
  return await systemClient.get("/role/page", { params });
};

// 查询角色（精简)列表
export const getSimpleRoleList = async (): Promise<RoleVO[]> => {
  return await systemClient.get("/role/simple-list");
};

// 查询角色详情
export const getRole = async (id: number) => {
  return await systemClient.get("/role/get?id=" + id);
};

// 新增角色
export const createRole = async (data: RoleForm) => {
  return await systemClient.post("/role/create", data);
};

// 修改角色
export const updateRole = async (data: RoleForm) => {
  return await systemClient.post("/role/update", data);
};

// 修改角色状态
export const updateRoleStatus = async (data: UpdateStatusForm) => {
  return await systemClient.post("/role/update-status", data);
};

// 删除角色
export const deleteRole = async (id: number) => {
  return await systemClient.post("/role/delete", { id });
};

// 批量删除角色
export const deleteRoleList = async (ids: number[]) => {
  return await systemClient.post("/role/delete-list", { ids });
};

// 删除角色下的用户
export const deleteRoleUser = async (roleId: number, userId: number) => {
  return await systemClient.post("/role/delete-role-user", { roleId, userId });
};

// 为角色下增加用户
export const addRoleUsers = async (roleId: number, userIds: number[]) => {
  return await systemClient.post("/role/add-users", { roleId, userIds });
};

// 移除某角色下的权限
export const removeRolePermission = async (
  roleId: number,
  permissionId: number,
) => {
  return await systemClient.post("/role/remove-permission", {
    roleId,
    permissionId,
  });
};

// 为某角色配置权限
export const configureRolePermissions = async (
  roleId: number,
  permissions: Record<number, number[]>,
) => {
  return await systemClient.post("/role/configure-permissions", {
    roleId,
    permissions,
  });
};

// 获取所有权限
export const getAllPermissions = async (
  params: Record<string, any> = {},
): Promise<Permission[]> => {
  return await systemClient.get("/permission/all", { params });
};

// 获取角色下已配置权限
export const getConfiguredPermissions = async (
  roleId: number,
): Promise<any> => {
  return await systemClient.get("/permission/configured", {
    params: { roleId },
  });
};

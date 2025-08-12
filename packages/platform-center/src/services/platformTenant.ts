import { systemService } from './clients';
import { PlatformTenantInfo, CreateTenantParams } from '../types/platformTenant';

// 可分配数量接口 /admin-api/system/tenant/get-allocatable-count
export const getCreateTenantCountApi = () => systemService.get('/tenant/get-allocatable-count');

// 获取平台租户列表信息
export const getPlatformTenantListApi = (params: {
  pageNo: number,
  pageSize?: number,
  keyword?: string,
  status?: number
}) => {
  const { pageNo, pageSize = 10, keyword, status = 2 } = params;

  let url = `/tenant/page?pageNo=${pageNo}&pageSize=${pageSize}`;

  if (keyword) {
    url += `&keyword=${encodeURIComponent(keyword)}`;
  }

  if (status !== undefined) {
    url += `&status=${status}`;
  }

  return systemService.get(url);
};

// 添加平台租户
export const addPlatformTenantApi = (data: CreateTenantParams) => systemService.post('/tenant/create', data);

// 修改平台租户
export const updatePlatformTenantApi = (data: any) => systemService.post('/tenant/update', data);

// 获取租户管理员列表
export const getPlatformTenantAdminListApi = () => systemService.get('/user/platform-admin/list');
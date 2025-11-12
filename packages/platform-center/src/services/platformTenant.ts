import { systemService } from './clients';
import { PlatformTenantInfo, CreateTenantParams, PlatformTenantSort } from '../types/platformTenant';

// 可分配数量接口 /admin-api/system/tenant/get-allocatable-count
export const getCreateTenantCountApi = () => systemService.get('/tenant/get-allocatable-count');

// 获取其他租户分配数量
export const getOtherTenantCountApi = (id?: string) => systemService.get('/tenant/get-other-exist-user-count', { id });

// 获取租户用户数量
export const getTenantUserCountApi = (id: string) => systemService.get('/tenant/get-tenant-exist-user-count', { id });

// 获取平台租户列表信息
export const getPlatformTenantListApi = (params: {
  pageNo: number,
  pageSize?: number,
  keywords?: string,
  status?: number | null,
  sortType?: PlatformTenantSort
}) => {
  const { pageNo, pageSize = 10, keywords, status, sortType } = params;

  let url = `/tenant/page?pageNo=${pageNo}&pageSize=${pageSize}`;

  if (keywords) {
    url += `&keyword=${encodeURIComponent(keywords)}`;
  }

  if (status !== null) {
    url += `&status=${status}`;
  }

  if (sortType !== null) {
    url += `&sortType=${sortType}`;
  }

  return systemService.get(url);
};

// 添加平台租户
export const addPlatformTenantApi = (data: CreateTenantParams) => systemService.post('/tenant/create', data);

// 修改平台租户
export const updatePlatformTenantApi = (data: any) => systemService.post('/tenant/update', data);

// 删除平台租户
export const deletePlatformTenantApi = (id: string) => systemService.post(`/tenant/delete?id=${id}`);

// 获取租户管理员列表
export const getPlatformTenantAdminListApi = () => systemService.get('/platform/admin/list');

// 获得租户(安全考虑仅获取用户所属租户)
export const getPlatformTenantAdminInfoApi = (id: string) => systemService.get(`/tenant/get?id=${id}`);

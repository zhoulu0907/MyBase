import type { TenantInfo, updateTenantParams } from '../types/tenant';
import { systemService } from './clients';

// 按id查询租户信息
export const getTenantInfo = (id: string): Promise<TenantInfo> => {
  return systemService.get(`/tenant/get?id=${id}`);
};

// 更新租户
export const updateTenant = (data: updateTenantParams): Promise<any> => {
  return systemService.post('/tenant/update', data);
};
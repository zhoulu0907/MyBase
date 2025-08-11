import type { TenantInfo } from '../types/tenant';
import { systemService } from './clients';

// 按id查询租户信息
export const getTenantInfo = (): Promise<TenantInfo> => {
  return systemService.get('/tenant/get');
};

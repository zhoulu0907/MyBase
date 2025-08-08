import systemClient from './clients/system';
import type { TenantInfo } from '../types/tenant';

// 按id查询租户信息
export const getTenantInfo = (): Promise<TenantInfo> => {
  return systemClient.get('/tenant/get');
};

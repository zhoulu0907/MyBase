import platformClient from './clients/platform';

// 获取平台租户列表信息
export const getPlatformTenantListApi = (params: any) => platformClient.get('/system/tenant/page', { params });

// 添加平台租户
export const addPlatformTenantApi = (data: any) => platformClient.post('/system/tenant', data);

// 修改平台租户
export const updatePlatformTenantApi = (data: any) => platformClient.put('/system/tenant', data);

// 根据状态筛选平台租户 全部/启用/禁止
export const getPlatformTenantListByStatusApi = (params: any) => platformClient.get('/system/tenant/status', { params });

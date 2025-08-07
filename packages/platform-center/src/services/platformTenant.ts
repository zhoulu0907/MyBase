import systemClient from './clients/system';

// 获取平台租户列表信息
export const getPlatformTenantListApi = (params: any) => systemClient.get('/system/tenant/page', { params });

// 添加平台租户
export const addPlatformTenantApi = (data: any) => systemClient.post('/system/tenant', data);

// 修改平台租户
export const updatePlatformTenantApi = (data: any) => systemClient.put('/system/tenant', data);

// 根据状态筛选平台租户 全部/启用/禁止
export const getPlatformTenantListByStatusApi = (params: any) => systemClient.get('/system/tenant/status', { params });

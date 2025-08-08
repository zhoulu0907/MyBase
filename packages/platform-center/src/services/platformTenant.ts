import systemClient from './clients/system';

// 获取平台租户列表信息
export const getPlatformTenantListApi = (params: any) => systemClient.get('/tenant/page', { params });

// 添加平台租户
export const addPlatformTenantApi = (data: any) => systemClient.post('/tenant/create', data);

// 修改平台租户
export const updatePlatformTenantApi = (data: any) => systemClient.post('/tenant/update', data);

// 获取租户管理员列表
export const getPlatformTenantAdminListApi = () => systemClient.get('/user/platform-admin/list');
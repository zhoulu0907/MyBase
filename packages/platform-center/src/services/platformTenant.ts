import { systemService } from './clients';

// 获取平台租户列表信息
export const getPlatformTenantListApi = (params: any) => systemService.get('/tenant/page', { params });

// 添加平台租户
export const addPlatformTenantApi = (data: any) => systemService.post('/tenant/create', data);

// 修改平台租户
export const updatePlatformTenantApi = (data: any) => systemService.post('/tenant/update', data);

// 获取租户管理员列表
export const getPlatformTenantAdminListApi = () => systemService.get('/user/platform-admin/list');
import { cratePlatformAdminReq } from '../types';
import { platformService } from './clients';

// 获取平台管理员列表信息
export const getPlatformAdminListApi = (params: any) =>
  platformService.get(
    `/platform/admin/page?pageNo=${params.pageNo}&pageSize=${params.pageSize}&keyword=${params.keyword}`
  );

// 新增平台管理员
export const createPlatformAdminApi = (data: cratePlatformAdminReq) =>
  platformService.post('/platform/admin/create', data);

export const getPlatformAdminInfoApi = (id: number | string) => platformService.get(`/platform/admin/get?id=${id}`);

// 删除平台管理员
export const deletePlatformAdminApi = (id: number | string) => platformService.post(`/platform/admin/delete?id=${id}`);

// 修改平台管理员邮箱
export const updatePlatformAdminMailApi = (data: { id: number | string; email: string }) =>
  platformService.post(`/platform/admin/update-email`, data);

// 修改平台管理员密码 /platform/update-password
export const updatePlatformAdminPasswordApi = (data: { id: number | string; password: string }) =>
  platformService.post(`/platform/admin/update-password`, data);

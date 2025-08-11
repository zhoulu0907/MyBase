import { cratePlatformAdminReq } from '../types';
import { systemService } from './clients';

// 获取平台管理员列表信息
export const getPlatformAdminListApi = (params: any) => systemService.get('/user/platform-admin/page', { params })

// 新增平台管理员
export const createPlatformAdminApi = (data: cratePlatformAdminReq) => systemService.post('/user/platform-admin/create', data)

// 删除平台管理员
export const deletePlatformAdminApi = (id: number | string) => systemService.post(`/user/platform-admin/delete?id=${id}`)

// 修改平台管理员邮箱
export const updatePlatformAdminMailApi = (data: {id: number | string, email: string}) => systemService.post(`/user/platform-admin/update-email`, data)

// 修改平台管理员密码 /user/update-platform-password
export const updatePlatformAdminPasswordApi = (data :{id: number | string, password: string}) => systemService.post(`/user/platform-admin/update-password`, data)
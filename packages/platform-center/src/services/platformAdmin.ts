import systemClient from './clients/system';

// 获取平台管理员列表信息
export const getPlatformAdminListApi = (params: any) => systemClient.get('/user/platform-admin/page', { params })

// 新增平台管理员
export const addPlatformAdminApi = (data: any) => systemClient.post('/user/platform-admin/create', data)

// 删除平台管理员
export const deletePlatformAdminApi = (id: number) => systemClient.delete(`/user/platform-admin/delete/${id}`)

// 修改平台管理员邮箱
export const updatePlatformAdminApi = (id: number, data: any) => systemClient.put(`/user/platform-admin/update/${id}`, data)

// 获取平台管理员密码
export const updatePlatformAdminPasswordApi = (id: number) => systemClient.get(`/user/platform-admin/password/${id}`)
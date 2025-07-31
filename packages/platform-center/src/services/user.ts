import { type PageParam } from '../types/common';
import { type UserVO } from '../types/user';
import systemClient from './clients/system';

// 查询用户管理列表
export const getUserPage = (params: PageParam) => {
  return systemClient.get('/user/page', { params })
}

// 查询用户详情
export const getUser = (id: number) => {
  return systemClient.get('/user/get?id=' + id)
}

// 新增用户
export const createUser = (data: UserVO) => {
  return systemClient.post('/user/create', data)
}

// 修改用户
export const updateUser = (data: UserVO) => {
  return systemClient.put('/user/update', data)
}

// 删除用户
export const deleteUser = (id: number) => {
  return systemClient.delete('/user/delete?id=' + id)
}

// 批量删除用户
export const deleteUserList = (ids: number[]) => {
  return systemClient.delete('/user/delete-list', { params: { ids: ids.join(',') }})
}

// 导出用户
export const exportUser = (fileName: string, params: any) => {
  return systemClient.download('/user/export', fileName, { params })
}

// 下载用户导入模板
export const importUserTemplate = () => {
  return systemClient.download('/user/get-import-template')
}

// 用户密码重置
export const resetUserPassword = (id: number, password: string) => {
  const data = {
    id,
    password
  }
  return systemClient.put('/user/update-password', data)
}

// 用户状态修改
export const updateUserStatus = (id: number, status: number) => {
  const data = {
    id,
    status
  }
  return systemClient.put('/user/update-status', data)
}

// 获取用户精简信息列表
export const getSimpleUserList = (): Promise<UserVO[]> => {
  return systemClient.get('/user/simple-list')
}
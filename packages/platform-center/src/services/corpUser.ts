import { type PageParam, PageResult } from '../types/common';
import type { UserProfileRespVO, UserProfileUpdatePwdReq, UserProfileUpdateReq, UserVO } from '../types/user';
import { corpService } from './clients';

// 查询用户管理列表
export const getUserPageInCorp = (params: PageParam) => {
  return corpService.get('/user/page', params);
};

// 查询用户详情
export const getUserInCorp = (id: number) => {
  return corpService.get('/user/get?id=' + id);
};

// 新增用户
export const createUserInCorp = (data: UserVO) => {
  return corpService.post('/user/create', data);
};

// 修改用户
export const updateUserInCorp = (data: UserVO) => {
  return corpService.post('/user/update', data);
};

// 删除用户
export const deleteUserInCorp = (id: number) => {
  return corpService.post('/user/delete?id=' + id);
};

// 批量删除用户
export const deleteUserListInCorp = (ids: number[]) => {
  return corpService.post('/user/post-list', {
    params: { ids: ids.join(',') }
  });
};

// 导出用户
export const exportUserInCorp = (fileName: string, params: any) => {
  return corpService.download('/user/export', fileName, { params });
};

// 下载用户导入模板
export const importUserTemplateInCorp = () => {
  return corpService.download('/user/get-import-template');
};

// 用户密码重置
export const resetUserPasswordInCorp = (id: number, password?: string) => {
  const data = {
    id,
    ...(password && { password }) // 只有当password存在时才添加到请求数据中
  };
  return corpService.post('/user/update-password', data);
};

// 用户状态修改
export const updateUserStatusInCorp = (id: number, status: number) => {
  const data = {
    id,
    status
  };
  return corpService.post('/user/update-status', data);
};

// 分页获取用户精简信息列表
export const getSimpleUserPageInCorp = (params: PageParam): Promise<PageResult<UserVO>> => {
  return corpService.get('/user/simple-list', params);
};

// 获得登录用户信息
export const getLoginedUserInCorp = (): Promise<UserProfileRespVO> => {
  return corpService.get('/user/profile/get');
};

// 修改用户个人信息
export const updateLoginedUserInCorp = (data: UserProfileUpdateReq) => {
  return corpService.post('/user/profile/update', data);
};

// 修改用户个人密码
export const updateLoginedUserPwdInCorp = (data: UserProfileUpdatePwdReq) => {
  return corpService.post('/user/profile/update-password', data);
};

// 获得用户列表-支持搜索 用于设置管理员和主管
export const getSimpleUserInCorp = (userNickName: string) => {
  return corpService.get(`/user/simple-list-by-name?userNickName=${userNickName}`);
};

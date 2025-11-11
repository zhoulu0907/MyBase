import { type PageParam, PageResult } from '../types/common';
import type { UserVO, UserProfileRespVO, UserProfileUpdateReq, UserProfileUpdatePwdReq } from '../types/user';
import { systemService } from './clients';

// 查询用户管理列表
export const getUserPage = (params: PageParam) => {
  return systemService.get('/user/page', params);
};

// 查询用户详情
export const getUser = (id: number) => {
  return systemService.get('/user/get?id=' + id);
};

// 新增用户
export const createUser = (data: UserVO) => {
  return systemService.post('/user/create', data);
};

// 修改用户
export const updateUser = (data: UserVO) => {
  return systemService.post('/user/update', data);
};

// 删除用户
export const deleteUser = (id: number) => {
  return systemService.post('/user/delete?id=' + id);
};

// 批量删除用户
export const deleteUserList = (ids: number[]) => {
  return systemService.post('/user/post-list', {
    params: { ids: ids.join(',') }
  });
};

// 导出用户
export const exportUser = (fileName: string, params: any) => {
  return systemService.download('/user/export', fileName, { params });
};

// 下载用户导入模板
export const importUserTemplate = () => {
  return systemService.download('/user/get-import-template');
};

// 用户密码重置
export const resetUserPassword = (id: number, password?: string) => {
  const data = {
    id,
    ...(password && { password }) // 只有当password存在时才添加到请求数据中
  };
  return systemService.post('/user/update-password', data);
};

// 用户状态修改
export const updateUserStatus = (id: number, status: number) => {
  const data = {
    id,
    status
  };
  return systemService.post('/user/update-status', data);
};

// 获取用户精简信息列表
export const getSimpleUserList = (): Promise<UserVO[]> => {
  return systemService.get('/user/simple-list');
};

// 分页获取用户精简信息列表
export const getSimpleUserPage = (params: PageParam): Promise<PageResult<UserVO>> => {
  return systemService.get('/user/simple-page', params);
};

// 获得登录用户信息
export const getLoginedUser = (): Promise<UserProfileRespVO> => {
  return systemService.get('/user/profile/get');
};

// 修改用户个人信息
export const updateLoginedUser = (data: UserProfileUpdateReq) => {
  return systemService.post('/user/profile/update', data);
};

// 修改用户个人密码
export const updateLoginedUserPwd = (data: UserProfileUpdatePwdReq) => {
  return systemService.post('/user/profile/update-password', data);
};

// 获得用户列表-支持搜索 用于设置管理员和主管
export const getSimpleUser = (userNickName: string) => {
  return systemService.get(`/user/simple-list-by-name?userNickName=${userNickName}`);
};

// 修改用户管理员/主管
export const updateAdminOrDirector = (data: UpdateAdminOrDirectorReq) => {
  return systemService.post('/dept/update-dept-admin-or-director', data);
};
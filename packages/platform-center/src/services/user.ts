import { isRuntimeEnv, PUBLISH_MODULE } from '@onebase/common';
import { type PageParam, PageResult } from '../types/common';
import type { UserProfileRespVO, UserProfileUpdatePwdReq, UserProfileUpdateReq, UserVO } from '../types/user';
import { runtimeCorpService, runtimeService, systemService } from './clients';

// 查询用户管理列表
export const getUserPage = (params: PageParam) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).get('/user/page', params);
};

// 查询用户详情
export const getUser = (id: string) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).get('/user/get?id=' + id);
};

// 新增用户
export const createUser = (data: UserVO) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).post('/user/create', data);
};

// 修改用户
export const updateUser = (data: UserVO) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).post('/user/update', data);
};

// 删除用户
export const deleteUser = (id: string) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).post('/user/delete?id=' + id);
};

// 批量删除用户
export const deleteUserList = (ids: string[]) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).post('/user/post-list', {
    params: { ids: ids.join(',') }
  });
};

// 导出用户
export const exportUser = (fileName: string, params: any) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).download('/user/export', fileName, { params });
};

// 下载用户导入模板
export const importUserTemplate = (runtime?: boolean) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).download('/user/get-import-template');
};

// 用户密码重置
export const resetUserPassword = (id: string, password?: string) => {
  const data = {
    id,
    ...(password && { password }) // 只有当password存在时才添加到请求数据中
  };
  return (isRuntimeEnv() ? runtimeCorpService : systemService).post('/user/update-password', data);
};

// 用户状态修改
export const updateUserStatus = (id: string, status: number) => {
  const data = {
    id,
    status
  };
  return (isRuntimeEnv() ? runtimeCorpService : systemService).post('/user/update-status', data);
};

// 获取用户精简信息列表
export const getSimpleUserList = (runtime?: boolean): Promise<UserVO[]> => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).get('/user/simple-list');
};

// 分页获取用户精简信息列表
export const getSimpleUserPage = (params: PageParam, loginMethod?: "mobile" | "username"): Promise<PageResult<UserVO>> => {
  return (loginMethod=== "mobile" ? runtimeCorpService : runtimeService).get('/user/simple-page', params);
};

// 获得登录用户信息
export const getLoginedUser = (runtime?: boolean): Promise<UserProfileRespVO> => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).get('/user/profile/get');
};

// 修改用户个人信息
export const updateLoginedUser = (data: UserProfileUpdateReq) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).post('/user/profile/update', data);
};

// 修改用户个人密码
export const updateLoginedUserPwd = (data: UserProfileUpdatePwdReq) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).post('/user/profile/update-password', data);
};

// 获得用户列表-支持搜索 用于设置管理员和主管
export const getUserListByName = (userNickName: string) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).get(
    `/user/simple-list-by-name?userNickName=${userNickName}`
  );
};

// 获得用户列表-支持搜索 用于设置管理员和主管
export const getSimpleUser = (deptId: string,directFlag: boolean) => {
  return (isRuntimeEnv() ? runtimeCorpService : systemService).get(
    `/user/simple-list-by-dept-id?deptId=${deptId}&directFlag=${directFlag}`
  );
};

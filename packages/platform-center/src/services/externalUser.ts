import {
  createExternalUserParams,
  externalUserListParams,
  Headers,
  loginConfigParams,
  pluginParams,
  thirdUserRegisterParams,
  updateExternalPwdParams,
  updateExternalUserParams,
  updateLoginConfigParams,
  updatePasswordParams,
  updateStatusParams,
  createExternalUserAppParams,
  forgotPWDParams
} from '../types';
import { systemService } from './clients';
import { runtimeService, runtimeUserService, userService } from './clients/factory';

//新增用户
export const createExternalUserApi = (data: createExternalUserParams) => systemService.post('/third-user/create', data);

//编辑用户
export const updateExternalUserApi = (data: updateExternalUserParams) => systemService.post('/third-user/update', data);

//删除用户
export const deleteExternalUserApi = (id: string) => systemService.post(`/third-user/delete?id=${id}`);

//获取用户列表-分页
export const getExternalUserListApi = (data: externalUserListParams) =>
  systemService.get('/third-user/user-applications-page', data);

//重置用户密码
export const updateExternalUserPwdApi = (data: updateExternalPwdParams) =>
  systemService.post('/third-user/update-password', data);

//获取外部用户授权应用
export const getAuthAppListApi = (userId?: string, appName?: string) =>
  userService.post(
    `/user-app-relation/user-no-relation-app-list?userId=${userId ? userId : ''}&appName=${appName ? appName : ''}`
  );

//修改用户状态
export const updateStatusApi = (data: updateStatusParams) => systemService.post('/third-user/update-status', data);

//获取第三方的部门列表
export const getExternalDeptListApi = () => systemService.get('/dept/get-third-depts');

//获得配置项列表-不分页
export const getPluginListApi = (data: pluginParams) => systemService.get('/config/list', data);

//修改状态-关闭/开启
export const updatePluginStatusApi = (id: string, status: number) =>
  systemService.post(`/config/update-status?id=${id}&status=${status}`);

//忘记密码- 外部用户
export const updatePasswordApi = (data: updatePasswordParams, headers: Headers) =>
  runtimeService.post('/third-user/forget-password', data, { headers });

//补充外部用户信息注册并登录
export const thirdUserRegisterApi = (data: thirdUserRegisterParams, headers: Headers) =>
  runtimeService.post('/third-user/register', data, { headers });

//配置参数 -应用发布登录页
export const loginConfigListByKeyApi = (data: loginConfigParams) =>
  systemService.get(`/config/list-by-keys?configType=app&appId=${data.appId}&configKeys=${data.configKeys}`);

//修改参数配置-应用发布登录页
export const updateLoginConfigApi = (data: updateLoginConfigParams) => systemService.post('/config/update', data);

//新增企业应用关联
export const createExternalUserApp = (data: createExternalUserAppParams) => runtimeUserService.post('/user-app-relation/create', data);

//忘记密码
export const forgotPWD = (data: forgotPWDParams, headers: Headers) => runtimeService.post('/auth/forget-password', data, { headers });

// 查询用户详情
export const getExternalUser = (id: string) => {
  return systemService.get('/third-user/get?id=' + id);
};
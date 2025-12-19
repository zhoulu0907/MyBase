
import { externalUserListParams, createExternalUserParams, updateExternalPwdParams, updateExternalUserParams, updateStatusParams, pluginParams, updatePasswordParams, registerExternalUserParams, supplementUserInfoParams } from "../types";
import { systemService } from "./clients";
import { userService } from "./clients/factory";

//新增用户
export const createExternalUserApi = (data: createExternalUserParams) => systemService.post('/third-user/create', data);

//编辑用户
export const updateExternalUserApi = (data: updateExternalUserParams) => systemService.post('/third-user/update', data);

//删除用户
export const deleteExternalUserApi = (id: string) => systemService.post(`/third-user/delete?id=${id}`);

//获取用户列表-分页
export const getExternalUserListApi = (data: externalUserListParams) => systemService.get('/third-user/user-applications-page', data);

//重置用户密码
export const updateExternalUserPwdApi = (data: updateExternalPwdParams) => systemService.post('/third-user/update-password', data);

//获取外部用户授权应用
export const getAuthAppListApi = (userId?: string,appName?: string) => userService.post(`/user-app-relation/user-no-relation-app-list?userId=${userId ? userId : ""}&appName=${appName ? appName : ""}`);

//修改用户状态
export const updateStatusApi = (data: updateStatusParams) => systemService.post('/third-user/update-status', data);

//获取第三方的部门列表
export const getExternalDeptListApi = () => systemService.get('/dept/get-third-depts');

//获得配置项列表-不分页
export const getPluginListApi = (data: pluginParams) => systemService.get('/config/list', data);

//修改状态-关闭/开启
export const updatePluginStatusApi = (id: string, status: number) => systemService.post(`/config/update-status?id=${id}&status=${status}`);

//忘记密码- 外部用户
export const updatePasswordApi = (data: updatePasswordParams) => systemService.post('/third-user/forget-password', data);

//注册外部用户
export const registerExternalUserApi = (data: registerExternalUserParams) => systemService.post('/third-user/register', data);

//补充外部用户信息
export const supplementUserInfoApi = (data: supplementUserInfoParams) => systemService.post('/third-user/supplement-user', data);
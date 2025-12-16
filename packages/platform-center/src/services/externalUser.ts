
import { externalUserListParams, createExternalUserParams, updateExternalPwdParams, updateExternalUserParams, updateStatusParams, pluginParams } from "../types";
import { systemService } from "./clients";

//新增用户
export const createExternalUserApi = (data: createExternalUserParams) => systemService.post('/third/create', data);

//编辑用户
export const updateExternalUserApi = (data: updateExternalUserParams) => systemService.post('/third/update', data);

//删除用户
export const deleteExternalUserApi = (id: string) => systemService.post(`/third/delete?id=${id}`);

//获取用户列表-分页
export const getExternalUserListApi = (data: externalUserListParams) => systemService.get('/third/user-applications-page', data);

//重置用户密码
export const updateExternalUserPwdApi = (data: updateExternalPwdParams) => systemService.post('/third/update', data);

//获取授权应用
export const getAuthAppListApi = (userId?: string,appName?: string) => systemService.post(`/third/update?userId=${userId}&appName=${appName}`);

//修改用户状态
export const updateStatusApi = (data: updateStatusParams) => systemService.post('/third/update-status', data);

//获取第三方的部门列表
export const getExternalDeptListApi = () => systemService.get('/dept/get-third-depts');

//获得配置项列表-不分页
export const getPluginListApi = (data: pluginParams) => systemService.get('/config/list', data);

//修改状态-关闭/开启
export const updatePluginStatusApi = (id: string, status: number) => systemService.post(`/config/update-status?id=${id}&status=${status}`);
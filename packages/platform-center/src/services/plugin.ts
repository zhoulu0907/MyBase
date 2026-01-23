import { pluginPageParams } from '../types';
import { pluginService } from './clients';

// 分页查询插件列表（动态插件）
export const getPluginPageListApi = (data: pluginPageParams) => pluginService.get('/info/page', data);

// 创建自定义插件
export const createPluginApi = (data: FormData) => pluginService.post('/info/create', data, {
  headers: {
    'Content-Type': 'multipart/form-data',
  },
});

// 获取插件详情
export const getPluginDetailApi = (id: string | number) => pluginService.get('/info/get', { id });

// 更新插件基础信息
export const updatePluginInfoApi = (data: FormData) => pluginService.post('/info/update', data, {
  headers: {
    'Content-Type': 'multipart/form-data',
  },
});

// 上传版本
export const uploadPluginVersionApi = (data: FormData) => pluginService.post('/version/upload', data, {
  headers: {
    'Content-Type': 'multipart/form-data',
  },
});

// 获取版本列表
export const getPluginVersionListApi = (pluginId: string) => pluginService.get('/version/list', { pluginId });

// 删除版本
export const deletePluginVersionApi = (id: string | number) => pluginService.post(`/version/delete?id=${id}`);

// 设为生效版本
export const activePluginVersionApi = (id: string | number) => pluginService.post(`/version/active?id=${id}`);

// 启用插件版本
export const enablePluginApi = (data: { pluginId: string; pluginVersion: string }) => pluginService.post('/info/enable', data);

// 禁用插件版本
export const disablePluginApi = (data: { pluginId: string; pluginVersion: string }) => pluginService.post('/info/disable', data);

// 获取插件配置模版
export const getPluginConfigTemplateApi = (params: { pluginId: string; pluginVersion: string }) => pluginService.get('/config/template', params);



import { pluginPageParams } from '../types';
import { pluginService } from './clients';

// 分页查询插件列表（动态插件）
export const getPluginPageListApi = (data: pluginPageParams) => pluginService.get('/info/page', data);


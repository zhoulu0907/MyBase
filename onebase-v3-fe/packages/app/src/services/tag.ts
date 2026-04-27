import { type CreateApplicationTagReq, type ListTagReq } from '../types/tag';
import { appService } from './clients';

// 应用标签列表
export const listApplicationTag = (params: ListTagReq) => {
  return appService.get('/tag/list', params);
};

// 应用标签分组统计
export const getApplicationTagGroupCount = () => {
  return appService.get('/tag/group-count');
}

// 更新应用标签集合
export const updateApplicationTag = (params: ListTagReq) => {
  return appService.post('/tag/update-tags', params);
};

// 创建应用标签
export const createApplicationTag = (params: ListTagReq) => {
  return appService.post('/tag/create', params);
};

// 删除应用标签
export const deleteApplicationTag = (tagId: string) => {
  return appService.post(`/tag/delete?tagId=${tagId}`);
};

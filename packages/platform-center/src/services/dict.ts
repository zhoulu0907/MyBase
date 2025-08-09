import systemClient from './clients/system';
import { DictItem, DictData, DictDataForm, DictForm } from '../types/dict';
import { PageParam, PageResult } from '../types/common';

/**
 * 创建字典
 */
export const createDict = (data: DictForm): Promise<void> => {
  return systemClient.post('/dict-type/create', data);
};

/**
 * 修改字典
 */
export const updateDict = (data: DictForm): Promise<void> => {
  return systemClient.post('/dict-type/update', data);
};

/**
 * 删除字典
 */
export const deleteDict = (id: number): Promise<void> => {
  return systemClient.post(`/dict-type/delete?id=${id}`);
};

/**
 * 获取字典列表-分页
 */
export const getDictListByPage = (params: PageParam): Promise<PageResult<DictItem>> => {
  return systemClient.get('/dict-type/page', params);
};

/**
 * 获取字典列表-不分页
 */
export const getAllDictList = (params?: Record<string, any>): Promise<DictItem[]> => {
  return systemClient.get('/dict-type/simple-list', params);
};

/**
 * 获取字典详情
 */
export const getDictDetail = (id: number): Promise<DictItem> => {
  return systemClient.get(`/dict-type/get?id=${id}`);
};

/**
 * 新增字典数据
 */
export const createDictData = (data: DictDataForm): Promise<void> => {
  return systemClient.post('/dict-data/create', data);
};

/**
 * 修改字典数据
 */
export const updateDictData = (data: DictDataForm): Promise<void> => {
  return systemClient.post('/dict-data/update', data);
};


/**
 * 更新字典数据状态
 */
export const updateDictDataStatus = (data: { id: number; status: number }): Promise<void> => {
  return systemClient.post('/dict-data/update-status', data);
};

/**
 * 删除字典数据
 */
export const deleteDictData = (id: number): Promise<void> => {
  return systemClient.post(`/dict-data/delete?id=${id}`);
};

/**
 * 获取字典数据列表-分页
 */
export const getDictDataListByPage = (params: PageParam & { dictType: string }): Promise<PageResult<DictData>> => {
  return systemClient.get('/dict-data/page', params);
};

/**
 * 获取字典数据详情
 */
export const getDictDataDetail = (id: number): Promise<DictData> => {
  return systemClient.get(`/dict-data/get?id=${id}`);
};

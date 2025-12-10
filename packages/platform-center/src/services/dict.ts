import { isRuntimeEnv } from '@onebase/common';
import { PageParam, PageResult } from '../types/common';
import { BatchConfigDictDataParams, DictData, DictDataForm, DictForm, DictItem } from '../types/dict';
import { runtimeService, systemService } from './clients';

/**
 * 创建字典
 */
export const createDict = (data: DictForm): Promise<void> => {
  return systemService.post('/dict-type/create', data);
};

/**
 * 修改字典
 */
export const updateDict = (data: DictForm): Promise<void> => {
  return systemService.post('/dict-type/update', data);
};

/**
 * 删除字典
 */
export const deleteDict = (id: string): Promise<void> => {
  return systemService.post(`/dict-type/delete?id=${id}`);
};

/**
 * 获取字典列表-分页
 */
export const getDictListByPage = (params: PageParam): Promise<PageResult<DictItem>> => {
  return systemService.get('/dict-type/page', params);
};

/**
 * 获取字典列表-不分页
 */
export const getAllDictList = (params?: Record<string, any>): Promise<DictItem[]> => {
  return systemService.get('/dict-type/simple-list', params);
};

/**
 * 获取字典详情
 */
export const getDictDetail = (id: string): Promise<DictItem> => {
  return systemService.get(`/dict-type/get?id=${id}`);
};

/**
 * 新增字典数据
 */
export const createDictData = (data: DictDataForm): Promise<void> => {
  return systemService.post('/dict-data/create', data);
};

/**
 * 修改字典数据
 */
export const updateDictData = (data: DictDataForm): Promise<void> => {
  return systemService.post('/dict-data/update', data);
};

/**
 * 更新字典数据状态
 */
export const updateDictDataStatus = (data: { id: string; status: number }): Promise<void> => {
  return systemService.post('/dict-data/update-status', data);
};

/**
 * 删除字典数据
 */
export const deleteDictData = (id: string): Promise<void> => {
  return systemService.post(`/dict-data/delete?id=${id}`);
};

/**
 * 获取字典数据列表-不分页
 */
export const getDictDataListByType = (dictType: string): Promise<DictData[]> => {
  return systemService.get(`/dict-data/simple-list-by-type?dictType=${dictType}`);
};

/**
 * 获取字典数据列表-分页
 */
export const getDictDataListByPage = (params: PageParam & { dictType: string }): Promise<PageResult<DictData>> => {
  return systemService.get('/dict-data/page', params);
};

/**
 * 获取字典数据详情
 */
export const getDictDataDetail = (id: string): Promise<DictData> => {
  return systemService.get(`/dict-data/get?id=${id}`);
};

/**
 * 批量配置字典数据
 */
export const batchConfigDictData = (data: BatchConfigDictDataParams): Promise<void> => {
  return systemService.post('/dict-data/batch-operate', data);
};

/**
 * 根据dict type获得字典数据列表
 */
export const getDictDataByType = (id: string): DictData[] => {
  return (isRuntimeEnv() ? runtimeService : systemService).get(`/dict-data/simple-list-by-type?id=${id}`);
};

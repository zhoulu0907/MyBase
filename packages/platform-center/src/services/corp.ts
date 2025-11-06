//企业管理
import { corpListParams, createCorpParams, corpStatusParams, updateCorpParams } from "../types"
import { systemService } from "./clients"

// 创建企业
export const createCorpApi = (data: createCorpParams) => systemService.post('/corp/create', data)

//更新企业
export const updateCorpApi = (data: updateCorpParams) => systemService.post('/corp/update', data);

//禁用/启用企业
export const disabledCorpApi = (data: corpStatusParams) => systemService.post(`/corp/update-status?id=${data.id}&status=${data.status}`);

// 删除企业
export const deleteCorpApi = (id: number) => systemService.post(`/corp/delete?id=${id}`)   

//获取企业列表-分页
export const getCorpListApi = (data: corpListParams) => systemService.get('/corp/page', data);

//获取企业精简信息列表-不分页
export const getCorpSimpleDetailsListApi = () => systemService.get('/corp/simple-list');

//获得详情
export const getDetailsApi = (id: number) => systemService.get(`/corp/get?id=${id}`);

//获取行业类型
export const getIndustryType = (type: string) => systemService.get(`dict-data/simple-list-by-type?dictType=${type}`);
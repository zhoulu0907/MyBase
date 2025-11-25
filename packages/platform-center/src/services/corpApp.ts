import { CorpAppParams, authAppStatusParams, corpAppListParams, updateAppParams } from "../types";
import { systemService } from "./clients";
import { appService, corpService } from "./clients/factory";

//新增企业应用
export const createCorpAppApi = (data: CorpAppParams) => systemService.post('/corp-app-relation/create', data);

//更新企业应用关联
export const updateCorpAppApi = (data: updateAppParams) => systemService.post('/corp-app-relation/update', data);

//删除应用授权企业
export const removeCorpAppApi = (id: number) => systemService.post(`/corp-app-relation/delete?id=${id}`);

//获得企业授权应用列表-分页
export const getCorpAuthorizedAppListApi = (data: corpAppListParams) => systemService.get('/corp-app-relation/corp-applications-page',data);

//获得企业关联应用分页
export const getCorpAppRelatedListApi = (data: corpAppListParams) => systemService.get('/corp-app-relation/page',data);

//获取应用列表
export const getCorpAppSimpleListApi = () => appService.get('/application/simple-list');

// 授权应用禁用启用
export const updateAuthAppStatus = (data: authAppStatusParams) => {
  return corpService.post(`/corp-app-relation/update-status?id=${data.id}&status=${data.status}`);
};
import { CorpAppParams, corpAppListParams } from "../types";
import { systemService } from "./clients";

//新增企业应用
export const createCorpAppApi = (data: CorpAppParams) => systemService.post('/corp-app-relation/create',data);

//更新企业应用关联
export const updateCorpAppApi = (data: CorpAppParams) => systemService.post('/corp-app-relation/update',data);

//删除应用授权企业
export const removeCorpAppApi = (id: number) => systemService.post('/corp-app-relation/update',id);

//获得企业授权应用列表-分页
export const getCorpAuthorizedAppListApi = (data: corpAppListParams) => systemService.post('/corp-app-relation/corp-applications-page',data);

//获得企业关联应用分页
export const getCorpAppRelatedListApi = (data: corpAppListParams) => systemService.post('/corp-app-relation/page',data);

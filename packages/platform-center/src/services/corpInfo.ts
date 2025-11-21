import { corpAppListParams, CorpDetailResponse, corpListParams, updateCorpParams } from "../types";
import { corpService } from "./clients";

//获得企业详情
export const getCorpDetailByIdApiInCorp = (id: string): CorpDetailResponse => corpService.get(`/get?id=${id}`);

//更新企业
export const updateCorpApiInCorp = (data: updateCorpParams) => corpService.post('/corp/update', data);


//获得企业授权应用列表-分页
export const getCorpAuthorizedAppListApiInCorp = (data: corpAppListParams) => corpService.get('/corp-app-relation/corp-applications-page',data);

//获取企业列表-分页
export const getCorpListApiInCorp = (data: corpListParams) => corpService.get('/corp/page', data);

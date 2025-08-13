// 实体管理服务
import { DataMethodParam, DeleteMethodParam, InsertMethodParams, PageMethodParam } from '../types';
import { metadataService } from './clients';


export const dataMethodInsert = (params: InsertMethodParams) => {
    return metadataService.post(`/data-method/insert`, params);
};

export const dataMethodPage = (params: PageMethodParam) => {
    return metadataService.post(`/data-method/data/page`, params);
};

export const dataMethodDelete = (params: DeleteMethodParam) => {
    return metadataService.post(`/data-method/delete`, params);
};

export const dataMethodData = (params: DataMethodParam) => {
    return metadataService.post(`/data-method/data`, params);
};
// 数据源服务

import type {
    DatasourceSaveReqVO,
    DatasourceTestConnectionReqVO,
    GetColumnsParams,
    GetDatasourcePageParams,
    GetTablesParams
} from '../types/dataresource';
import systemService from './clients/app';

/**
 * 获取所有支持的数据源类型
 * @returns 数据源类型列表
 */
export const getDatasourceTypes = () => {
  return systemService.post("/metadata/datasource/types");
};

/**
 * 根据数据源ID查询表名列表
 * @param params 查询参数
 * @returns 表信息列表
 */
export const getTables = (params: GetTablesParams) => {
  return systemService.post("/metadata/datasource/tables", { params });
};

/**
 * 根据表名查询字段信息
 * @param params 查询参数
 * @returns 字段信息列表
 */
export const getColumns = (params: GetColumnsParams) => {
  return systemService.post("/metadata/datasource/columns", { params });
};

/**
 * 新增数据源
 * @param data 数据源信息
 * @returns 数据源ID
 */
export const createDatasource = (data: DatasourceSaveReqVO) => {
  return systemService.post('/metadata/datasource/create', data);
};

/**
 * 修改数据源
 * @param data 数据源信息
 * @returns 操作结果
 */
export const updateDatasource = (data: DatasourceSaveReqVO) => {
  return systemService.post("/metadata/datasource/update", data);
};

/**
 * 删除数据源
 * @param id 数据源ID
 * @returns 操作结果
 */
export const deleteDatasource = (id: number) => {
  return systemService.post("/metadata/datasource/delete", {
    params: { id },
  });
};

/**
 * 获得数据源详情
 * @param id 数据源ID
 * @returns 数据源详情
 */
export const getDatasource = (id: number) => {
  return systemService.post("/metadata/datasource/get", { params: { id } });
};

/**
 * 获得数据源分页列表
 * @param params 分页查询参数
 * @returns 数据源分页列表
 */
export const getDatasourcePage = (params: GetDatasourcePageParams) => {
  return systemService.post("/metadata/datasource/page", params);
};

/**
 * 获得数据源列表
 * @returns 数据源列表
 */
export const getDatasourceList = () => {
  return systemService.post("/metadata/datasource/list");
};

/**
 * 根据编码获得数据源
 * @param code 数据源编码
 * @returns 数据源详情
 */
export const getDatasourceByCode = (code: string) => {
  return systemService.post("/metadata/datasource/get-by-code", {
    params: { code },
  });
};

/**
 * 测试数据源连接
 * @param data 测试连接参数
 * @returns 测试结果
 */
export const testDatasourceConnection = (data: DatasourceTestConnectionReqVO) => {
  return systemService.post('/metadata/datasource/test-connection', data);
};

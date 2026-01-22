import { type PageParam } from '../types/common';
import {
  CreateConnectInstanceReq,
  CreateFlowMgmtReq,
  CreateScriptActionReq,
  ListConnectFlowNodeReq,
  ListConnectInstanceReq,
  ListScriptActionReq,
  RenameFlowMgmtReq,
  UpdateConnectInstanceReq,
  UpdateFlowMgmtDefinitionReq,
  UpdateScriptActionReq
} from '../types/flow';
import { flowService } from './clients';

export const listFlowMgmt = (params: PageParam) => {
  return flowService.get('/mgmt/page', params);
};

export const getFlowMgmt = (id: string) => {
  return flowService.get(`/mgmt/get?id=${id}`);
};

export const createFlowMgmt = (params: CreateFlowMgmtReq) => {
  return flowService.post('/mgmt/create', params);
};

export const updateFlowMgmt = (params: CreateFlowMgmtReq) => {
  return flowService.post('/mgmt/update', params);
};

export const deleteFlowMgmt = (id: string) => {
  return flowService.post(`/mgmt/delete?id=${id}`);
};

export const batchDeleteFlowMgmt = (ids: string[]) => {
  return flowService.post('/mgmt/batch-delete', { ids });
};

export const enableFlowMgmt = (id: string) => {
  return flowService.post(`/mgmt/enable?id=${id}`);
};

export const disableFlowMgmt = (id: string) => {
  return flowService.post(`/mgmt/disable?id=${id}`);
};

export const renameFlowMgmt = (params: RenameFlowMgmtReq) => {
  return flowService.post('/mgmt/rename', params);
};

export const updateFlowMgmtDefinition = (params: UpdateFlowMgmtDefinitionReq) => {
  return flowService.post('/mgmt/update-definition', params);
};

export const refreshFlowMgmt = (appId: string) => {
  return flowService.post(`/exec/flow-handler/update?applicationId=${appId}`);
};

// 分页查询执行日志
export const getFlowLogPage = (params: any) => {
  return flowService.get('/log/page', params);
};

// 获取日志详情
export const getFlowLogDetail = (params: any) => {
  return flowService.get('/log/get', params);
};

// 统计执行日志
export const getFlowLogStatistic = (params: any) => {
  return flowService.get('/log/statistic-tody', params);
};

export const getConnectFlowNodeCategoryList = () => {
  return flowService.get('/node-category/list');
};

export const listConnectFlowNode = (params: ListConnectFlowNodeReq) => {
  return flowService.get('/node-type/page', params);
};

/**
 * 获取连接器类型列表（全部）
 * 使用 pageSize: -1 获取全量数据
 */
export const getConnectorNodeTypesAll = () => {
  return flowService.get('/node-type/page', {
    pageNo: 1,
    pageSize: -1
  });
};

export const listConnectInstance = (params: ListConnectInstanceReq) => {
  return flowService.get('/connector/page', params);
};

/**
 * 获取所有连接器实例（支持分页）
 * 调用 /flow/connector/list-all，支持 pageNo 和 pageSize 参数
 */
export const listAllConnectInstance = (params?: { pageNo?: number; pageSize?: number }) => {
  return flowService.get('/connector/list-all', params);
};

export const createConnectInstance = (params: CreateConnectInstanceReq) => {
  return flowService.post('/connector/create', params);
};

export const getConnectInstance = (id: string) => {
  return flowService.get(`/connector/get?id=${id}`);
};

/**
 * 通过 connectorUuid 获取连接器实例详情
 * @param connectorUuid 连接器实例的 UUID
 */
export const getConnectorByUuid = (connectorUuid: string) => {
  return flowService.get(`/connector/get?connectorUuid=${connectorUuid}`);
};

export const updateConnectInstance = (params: UpdateConnectInstanceReq) => {
  return flowService.post('/connector/update', params);
};

export const deleteConnectInstance = (id: string) => {
  return flowService.post(`/connector/delete?id=${id}`);
};

export const getScriptAction = (id: string) => {
  return flowService.get(`/connector/script/get?id=${id}`);
};

export const listScriptAction = (params: ListScriptActionReq) => {
  return flowService.get('/connector/script/page', params);
};

export const createScriptAction = (params: CreateScriptActionReq) => {
  return flowService.post('/connector/script/create', params);
};

export const updateScriptAction = (params: UpdateScriptActionReq) => {
  return flowService.post('/connector/script/update', params);
};

export const deleteScriptAction = (id: string) => {
  return flowService.post(`/connector/script/delete?id=${id}`);
};

/**
 * 获取连接器节点类型列表（用于连接器类型页面）
 * 从 /flow/node-config/node-types 接口获取所有可用的连接器类型
 */
export const getConnectorNodeTypes = () => {
  return flowService.get('/node-config/node-types');
};

/**
 * 获取连接器节点类型详细信息
 * 从 /flow/node-config/type-info 接口获取指定连接器类型的详细信息
 * @param nodeCode 连接器类型代码，如 'weaverE9'
 */
export const getConnectorTypeInfo = (nodeCode: string) => {
  return flowService.get(`/node-config/type-info?nodeCode=${nodeCode}`);
};

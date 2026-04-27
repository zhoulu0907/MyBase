import { type PageParam } from '../types/common';
import {
  CreateConnectInstanceReq,
  CreateConnectorActionReq,
  CreateFlowMgmtReq,
  CreateScriptActionReq,
  DebugActionReq,
  GetActionValueReq,
  ListConnectFlowNodeReq,
  ListConnectInstanceReq,
  ListConnectorActionReq,
  ListConnectorByTypeReq,
  ListScriptActionReq,
  RenameFlowMgmtReq,
  SaveConnectorActionReq,
  UpdateConnectInstanceReq,
  UpdateConnectorActionReq,
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
export const listAllConnectInstance = (params?: { pageNo?: number; pageSize?: number; connectorName?: string }) => {
  return flowService.get('/connector/list-all', params);
};

export const createConnectInstance = (params: CreateConnectInstanceReq) => {
  return flowService.post('/connector/create', params);
};

export const getConnectInstance = (id: string) => {
  return flowService.get(`/connector/${id}`);
};

/**
 * @deprecated 接口已废弃，统一使用 getConnectInstance(id)
 */
export const getConnectorByUuid = (connectorUuid: string) => {
  console.error('getConnectorByUuid is deprecated, please use getConnectInstance(id)');
  return flowService.get(`/connector/detail?connectorUuid=${connectorUuid}`);
};

export const updateConnectInstance = (params: UpdateConnectInstanceReq) => {
  const { id, ...data } = params;
  return flowService.post(`/connector/${id}/update`, data);
};

export const updateConnectInstanceBaseInfo = (params: UpdateConnectInstanceReq) => {
  const { id, ...data } = params;
  return flowService.post(`/connector/${id}/update-base-info`, data);
};

export const deleteConnectInstance = (id: string) => {
  return flowService.post(`/connector/${id}/delete`);
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

export const listConnectorNodeConfig = () => {
  return flowService.get('/node-config/list-all');
};

export const listConnectorByType = (params: ListConnectorByTypeReq) => {
  return flowService.get(`/connector/by-type/${params.typeCode}`);
};

export const listConnectorActions = (params: ListConnectorActionReq) => {
  return flowService.get(`/connector/${params.id}/actions`);
};

export const listConnectorActionInfos = (params: ListConnectorActionReq) => {
  return flowService.get(`/connector/${params.id}/action-infos`);
};

export const getConnectorActionInfo = (id: string, actionName: string) => {
  return flowService.get(`/connector/${id}/actions/${actionName}`);
};

/** 获取连接器动作详情（通过 connectorUuid + actionCode） */
export const getConnectorActionByCode = (connectorUuid: string, actionCode: string) => {
  return flowService.get('/connector-action/detail-by-code', { connectorUuid, actionCode });
};

/** 获取连接器动作详情（通过ID） */
export const getConnectorActionById = (id: number) => {
  return flowService.get('/connector-action/detail', { id });
};

/** 创建连接器动作 */
export const createConnectorAction = (params: CreateConnectorActionReq) => {
  return flowService.post('/connector-action/create', params);
};

/** 更新连接器动作 */
export const updateConnectorAction = (params: UpdateConnectorActionReq) => {
  return flowService.post('/connector-action/update', params);
};

/** 删除连接器动作 */
export const deleteConnectorAction = (id: number) => {
  return flowService.post('/connector-action/delete', { id });
};

/** 获取连接器动作列表 */
export const listConnectorActionsByUuid = (connectorUuid: string) => {
  return flowService.get('/connector-action/list', { connectorUuid });
};

/** 更新连接器动作状态 */
export const updateConnectorActionStatus = (id: number, activeStatus: number) => {
  return flowService.post('/connector-action/update-status', { id, activeStatus });
};

/** @deprecated 使用 createConnectorAction 代替 */
export const saveConnectorAction = (id: string, params: SaveConnectorActionReq) => {
  return flowService.post(`/connector/${id}/save-action`, params);
};

export const getActionValue = (id: string, params: GetActionValueReq) => {
  return flowService.get(`/connector/${id}/action-value`, params);
};

/** @deprecated 使用 deleteConnectorAction 代替 */
export const deleteHTTPAction = (id: string, actionName: string) => {
  return flowService.post(`/connector/${id}/actions/${actionName}/delete`);
};

/** @deprecated 使用 updateConnectorAction 代替 */
export const updateHTTPAction = (id: string, actionName: string, params: SaveConnectorActionReq) => {
  return flowService.post(`/connector/${id}/actions/${actionName}/update-config`, params);
};

export const debugAction = (params: DebugActionReq) => {
  return flowService.post(`/connector/debug-http-action`, params);
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

// ============ 连接器环境配置 API（新接口） ============

/**
 * 获取连接器环境配置列表
 * @param connectorId 连接器实例ID
 */
export const getConnectorEnvList = (connectorId: string | number) => {
  return flowService.get('/connector-env/list', { connectorId });
};

/**
 * 获取指定环境配置详情
 * @param connectorId 连接器实例ID
 * @param envCode 环境编码
 */
export const getConnectgorEnvironmentConfig = (connectorId: string | number, envCode: string) => {
  return flowService.get('/connector-env/detail', { connectorId, envCode });
};

/**
 * 获取环境配置模板
 * @param connectorId 连接器实例ID
 */
export const getEnvConfigTemplate = (connectorId: string | number) => {
  return flowService.get('/connector-env/template', { connectorId });
};

/**
 * 创建环境配置
 * @param connectorId 连接器实例ID
 * @param params 环境配置请求
 */
export const createConnectorEnv = (connectorId: string | number, params: any) => {
  return flowService.post(`/connector-env/create?connectorId=${connectorId}`, params);
};

/**
 * 更新环境配置
 * @param connectorId 连接器实例ID
 * @param params 环境配置请求
 */
export const updateEnvironmentConfig = (connectorId: string | number, params: any) => {
  return flowService.post(`/connector-env/update?connectorId=${connectorId}`, params);
};

/**
 * 设置启用环境
 * @param connectorId 连接器实例ID
 * @param envCode 环境编码（传空表示取消启用）
 */
export const enableConnectorEnvironment = (connectorId: string | number, envCode?: string) => {
  const query = envCode ? `?connectorId=${connectorId}&envCode=${encodeURIComponent(envCode)}` : `?connectorId=${connectorId}`;
  return flowService.post(`/connector-env/enable${query}`);
};

/**
 * 获取启用的环境信息
 * @param connectorId 连接器实例ID
 * @returns FlowConnectorEnvLiteVO 或 null
 */
export const getEnableConnectorEnvironment = (connectorId: string | number) => {
  return flowService.get('/connector-env/enabled-env', { connectorId });
};

// ============ 以下为废弃接口，保留向后兼容 ============

/** @deprecated 使用 getConnectorEnvList 代替 */
export const getConnectorEnvDetail = (id: string) => {
  return flowService.get(`/connector-env/${id}`);
};

/** @deprecated 使用 updateEnvironmentConfig 代替 */
export const updateConnectorEnv = (params: any) => {
  return flowService.post('/connector-env/update', params);
};

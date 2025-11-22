import { type PageParam } from '../types/common';
import {
  CreateConnectInstanceReq,
  CreateFlowMgmtReq,
  ListConnectFlowNodeReq,
  ListConnectInstanceReq,
  RenameFlowMgmtReq,
  UpdateFlowMgmtDefinitionReq
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

export const listConnectInstance = (params: ListConnectInstanceReq) => {
  return flowService.get('/connector/page', params);
};

export const createConnectInstance = (params: CreateConnectInstanceReq) => {
  return flowService.post('/connector/create', params);
};

export const getConnectInstance = (id: string) => {
  return flowService.get(`/connector/get?id=${id}`);
};

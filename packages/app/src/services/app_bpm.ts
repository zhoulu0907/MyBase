import { bpmService,runtimeBpmService } from './clients';
import { 
  GetByBusinessId,
  SaveRequest,
  PublishRequest,
  VersionMgmtRequest,
  UpdateVersionAlias,
  GetFlowPreview,
  AgentPage,
  AgentCreate
  } from '../types/app_bpm';
export const getByBusinessUuid = (params:GetByBusinessId) => {
  return bpmService.get('/design/get-by-business-uuid', params);
};
export const getDataById = (id: {id: string}) => {
  return bpmService.get('/design/get', id);
};


export const save = (params:SaveRequest) => {
  return bpmService.post('/design/save', params);
};
export const fetchPublish = (params: PublishRequest) => {
  return bpmService.post('/design/publish', params);
};

export const getVersionMgmt = (params: VersionMgmtRequest) => {
  return bpmService.get('/version-mgmt/page', params);
};

export const getFlowPreview = (params: GetFlowPreview) => {
  return runtimeBpmService.get('/instance/flow-preview', params)
} 
export const versionMgmtDelete = (params: PublishRequest) => {
  return bpmService.post('/version-mgmt/delete', params);
};
export const updateVersionAlias = (params: UpdateVersionAlias) => {
  return bpmService.post('/version-mgmt/update-version-alias', params);
};

export const agentPage = (params: AgentPage) => {
  return runtimeBpmService.get('/agent/page',params);
};

export const agentCreate = (params: AgentCreate) => {
  return runtimeBpmService.post('/agent/create',params);
};

export const agentUpdate = (params: AgentCreate) => {
  return runtimeBpmService.post('agent/update',params);
};

export const agentRevoke = (params: {id:string}) => {
  return runtimeBpmService.post('agent/revoke',params);
};

export const getFormDataPage = (params: any) => {
  return runtimeBpmService.post('/instance/form-data-page', params);
};
export const deleteFormDataPage = (params: any) => {
  return runtimeBpmService.post('/instance/delete-form-data', params);
};





import { bpmService,runtimeBpmService } from './clients';
import type { GetByBusinessId, SaveRequest, PublishRequest,VersionMgmtRequest,GetFlowPreview} from '../types/app_bpm';
export const getByBusinessId = (params:GetByBusinessId) => {
  return bpmService.get('/design/get-by-business-id', params);
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
  return runtimeBpmService.get('/instance/flow-preview', params);
};


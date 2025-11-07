import { bpmService } from './clients';
import { GetByBusinessId, SaveRequest, PublishRequest,VersionMgmtRequest } from '../types/app_bpm';
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

import { bpmService } from './clients';
import { GetByBusinessId, SaveRequest, PublishRequest } from '../types/app_bpm';
export const getByBusinessId = (params:GetByBusinessId) => {
  return bpmService.get('/design/get-by-business-id', params);
};

export const save = (params:SaveRequest) => {
  return bpmService.post('/design/save', params);
};
export const fetchPublish = (params: PublishRequest) => {
  return bpmService.post('/design/publish', params);
};

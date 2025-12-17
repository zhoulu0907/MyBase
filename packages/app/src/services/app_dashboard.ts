import { appService, runtimeAppService } from './clients';
import { CreateScreenApiParams } from '../types/app_dashboard';

export const createScreenApi = (params: CreateScreenApiParams) => {
  return appService.post('/api/goview/project/create', params);
};
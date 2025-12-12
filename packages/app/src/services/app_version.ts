import {
  OfflineApplicationReq,
  OnlineApplicationReq,
  type DeleteApplicationVersionReq,
  type PageApplicationVersionReq,
  type RestoreApplicationVersionReq
} from '../types/app_version';
import { appService } from './clients';

export const pageApplicationVersion = (params: PageApplicationVersionReq) => {
  return appService.get('/version/page', params);
};

export const onlineApplication = (params: OnlineApplicationReq) => {
  return appService.post('/version/online', params);
};

export const offlineApplication = (params: OfflineApplicationReq) => {
  return appService.post('/version/offline', params);
};

export const restoreApplicationVersion = (params: RestoreApplicationVersionReq) => {
  return appService.post('/version/restore', params);
};

export const deleteApplicationVersion = (params: DeleteApplicationVersionReq) => {
  return appService.post('/version/delete', params);
};

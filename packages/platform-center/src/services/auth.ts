import { LoginRequest } from '../types';
import { systemService } from './clients';

export const login = (req: LoginRequest) => {
  return systemService.post('/auth/login', req);
};

export const getPermissionInfo = () => {
  return systemService.get('/auth/get-permission-info');
};

import { LoginRequest, Headers } from '../types';
import { systemService } from './clients';

export const login = (req: LoginRequest, headers: Headers) => {
  return systemService.post('/auth/login', req, { headers });
};

export const getPermissionInfo = () => {
  return systemService.get('/auth/get-permission-info');
};

export const adminLogin = (req: LoginRequest) => {
  return systemService.post('/auth/admin-login', req);
};

export const logout = () => {
  return systemService.post('/auth/logout');
};

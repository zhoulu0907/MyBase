import { type Captcha, type CaptchaCheck } from '@onebase/common';
import { Headers, LoginRequest, TenantLoginRequest } from '../types';
import { platformService, runtimeService, systemService } from './clients';

export const login = (req: LoginRequest, headers: Headers) => {
  return platformService.post('/auth/login', req, { headers });
};

export const tenantLogin = (req: TenantLoginRequest, headers: Headers) => {
  return systemService.post('/auth/tenant-login', req, { headers });
};

export const getPermissionInfo = () => {
  return systemService.get('/auth/get-permission-info');
};

// export const adminLogin = (req: LoginRequest, headers: Headers) => {
//   return systemService.post('/auth/admin-login', req, { headers });
// };

export const adminLogin = (req: LoginRequest, headers: Headers) => {
  return platformService.post('/auth/login', req, { headers });
};

export const platformLogout = () => {
  return platformService.post('/auth/logout');
};

export const systemLogout = () => {
  return systemService.post('/auth/logout');
};

export const runtimeLogout = () => {
  return runtimeService.post('/auth/logout');
};

// 获取验证码 /system/captcha/get
export const getCaptchaApi = (data: Captcha) => {
  return systemService.post('/captcha/get', data);
};

// 校验验证码 /system/captcha/check
export const checkCaptchaApi = (data: CaptchaCheck) => {
  return systemService.post('/captcha/check', data);
};

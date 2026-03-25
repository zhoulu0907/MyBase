import { isRuntimeEnv, type Captcha, type CaptchaCheck } from '@onebase/common';
import {
  CodeType,
  Headers,
  LoginRequest,
  OAuthAuthorizeRequest,
  OAuthAuthorizeResponse,
  RuntimeAccountLoginRequest,
  RuntimeCorpLoginRequest,
  RuntimeMobileLoginRequest,
  RuntimeThirdLoginRequest,
  SendVerifyCodeRequest,
  TenantLoginRequest,
  TiangongLoginRequest,
  TiangongLoginResponse
} from '../types';
import { platformService, runtimeService, systemService } from './clients';

export const login = (req: LoginRequest, headers: Headers) => {
  return platformService.post('/auth/login', req, { headers });
};

export const tenantLogin = (req: TenantLoginRequest, headers: Headers) => {
  return systemService.post('/auth/tenant-login', req, { headers });
};

export const getPermissionInfo = (code?: CodeType) => {
  return (isRuntimeEnv() ? runtimeService : systemService).get(`/auth/get-permission-info?code=${code}`);
};

export const adminLogin = (req: LoginRequest, headers: Headers) => {
  return platformService.post('/auth/login', req, { headers });
};

export const innerLogin = (req: RuntimeAccountLoginRequest, headers: Headers) => {
  return runtimeService.post('/auth/app-login', req, { headers });
};

export const sassLogin = (req: RuntimeMobileLoginRequest, headers: Headers) => {
  return runtimeService.post('/auth/app-login-mobile', req, { headers });
};

export const runtimeCorpLogin = (req: RuntimeCorpLoginRequest, headers: Headers) => {
  return runtimeService.post('/auth/corp-login', req, { headers });
};

export const runtimeThirdLogin = (req: RuntimeThirdLoginRequest, headers: Headers) => {
  return runtimeService.post('/auth/third-login', req, { headers });
};

// TODO(mickey): 重构合并

export const platformLogout = () => {
  return platformService.post('/auth/logout');
};

export const systemLogout = () => {
  return systemService.post('/auth/logout');
};

export const runtimeLogout = () => {
  return runtimeService.post('/auth/logout');
};

export const sendVerifyCodeApi = (data: SendVerifyCodeRequest) => {
  return (isRuntimeEnv() ? runtimeService : systemService).post('/auth/send-verify-code', data);
};

// 获取验证码 /system/captcha/get
export const getCaptchaApi = (data: Captcha) => {
  return (isRuntimeEnv() ? runtimeService : systemService).post('/captcha/get', data);
};

export const tiangongLogin = (req: TiangongLoginRequest): Promise<TiangongLoginResponse> => {
  const { code, deviceId } = req;
  return systemService.get('/auth/tiangong-login', {
    code,
    deviceId
  });
};

export const oauthAuthorize = (req: OAuthAuthorizeRequest): Promise<OAuthAuthorizeResponse> => {
  const params = new URLSearchParams();
  params.append('client_id', req.client_id);
  params.append('scope', req.scope);
  params.append('redirect_uri', req.redirect_uri);
  params.append('response_type', req.response_type);
  if (req.auto_approve !== undefined) {
    params.append('auto_approve', String(req.auto_approve));
  }
  return systemService.post(`/oauth2/authorize/code?${params.toString()}`);
};

// 校验验证码 /system/captcha/check
export const checkCaptchaApi = (data: CaptchaCheck) => {
  return (isRuntimeEnv() ? runtimeService : systemService).post('/captcha/check', data);
};

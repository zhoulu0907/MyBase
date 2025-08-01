import{ httpPost } from '@/utils/http';
import type { LoginRequest, LoginResponse } from '@/types/login'

// 登录接口
export const loginApi = async (data: LoginRequest): Promise<LoginResponse> => {
  const response = await httpPost<LoginResponse>('/system/auth/login', data, {
    headers: {
      'Tenant-Id': '1'
    }
  });
  return response;
};

// 刷新token接口
export const refreshTokenApi = async (refreshToken: string): Promise<{ accessToken: string }> => {
  const response = await httpPost<{ accessToken: string }>('/system/auth/refresh-token', {
    refreshToken
  });
  return response;
};

// 登出接口
export const logoutApi = async (): Promise<void> => {
  await httpPost('/system/auth/logout');
};

export default {
  loginApi,
  refreshTokenApi,
  logoutApi
};
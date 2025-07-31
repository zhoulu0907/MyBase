import{ httpPost } from '@/utils/http';
import type { LoginRequest, LoginResponse } from '@/types/login'

// 登录接口
export const login = async (data: LoginRequest): Promise<LoginResponse> => {
  const response = await httpPost<LoginResponse>('/system/auth/login', data);
  return response;
};

// 刷新token接口
export const refreshToken = async (refreshToken: string): Promise<{ accessToken: string }> => {
  const response = await httpPost<{ accessToken: string }>('/system/auth/refresh-token', {
    refreshToken
  });
  return response;
};

// 登出接口
export const logout = async (): Promise<void> => {
  await httpPost('/system/auth/logout');
};

export default {
  login,
  refreshToken,
  logout
};
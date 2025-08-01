// 登录相关类型定义

export interface LoginRequest {
  username: string;
  password: string;
  account: string;
  // captchaCode?: string;
  // captchaUuid?: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userId: string;
  username: string;
  tenantId: string;
  tenantName: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse {
  accessToken: string;
}
// 获取验证码请求参数
/**
 * 登录请求参数
 */
export interface LoginRequest {
  /**
   * 账号
   */
  username: string;
  /**
   * 密码
   */
  password: string;
  /**
   * 验证码 Token
   */
  captcha_token?: string | null;
}

export interface LoginResponse {
  userId: number;                  // 用户ID
  accessToken: string;             // 访问令牌
  refreshToken: string;            // 刷新令牌
  expiresTime: number;             // 令牌过期时间（时间戳，毫秒）
}

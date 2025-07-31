// 获取验证码请求参数
/**
 * 登录请求参数
 */
export interface LoginRequest {
  /**
   * 账号
   */
  account: string;
  /**
   * 密码
   */
  password: string;
  /**
   * 验证码 Token
   */
  captcha_token: string;
}

export interface LoginResponse {
  is_login: boolean;               // 是否已登录
  account: string;                 // 账号
  account_id: number;              // 登录ID
  username: string;                // 用户名
  token_name: string;              // 令牌名称
  token_value: string;             // 令牌值
  expires_in: number;              // 令牌超时时间（秒）
}

/**
 * SM2 公钥响应接口
 */
export interface Sm2PublicKeyResponse {
    /** 公钥 */
    public_key: string;
}
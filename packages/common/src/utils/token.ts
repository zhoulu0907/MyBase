/**
 * Token 管理工具类
 * 提供 token 的存储、获取、删除和验证功能
 */

import { getEnv } from './env';

export interface TokenInfo {
  userId: string; // 用户ID
  accessToken: string; // 访问令牌
  refreshToken: string; // 刷新令牌
  expiresTime: number; // 令牌过期时间（时间戳，毫秒）
  tenantId?: string; // 租户id
  corpId?: string; // 企业id
  adminFlag?: boolean; // 是否是管理员
  loginURL?: string;
}

export class TokenManager {
  private static readonly TOKEN_KEY = 'onebase_token';
  private static readonly TOKEN_INFO_KEY = 'onebase_token_info';
  private static readonly REMEMBER_ME_KEY = 'onebase_remember_me';
  private static readonly TENANT_ID = 'tenant_id';
  private static readonly LOGIN_URL = 'login_url';

  static addEnv(key: string): string {
    return `${getEnv()}_${key}`;
  }

  /**
   * 存储 token 信息
   * @param tokenInfo token信息
   * @param rememberMe 是否记住我
   */
  static setToken(tokenInfo: TokenInfo, rememberMe: boolean = false): void {
    try {
      // 根据记住我选项选择存储方式
      if (rememberMe) {
        // 记住我：使用 localStorage（持久化存储）
        localStorage.setItem(this.addEnv(this.TOKEN_KEY), tokenInfo.accessToken);
        localStorage.setItem(this.addEnv(this.TOKEN_INFO_KEY), JSON.stringify(tokenInfo));
        localStorage.setItem(this.addEnv(this.REMEMBER_ME_KEY), 'true');
        if (tokenInfo.tenantId) {
          localStorage.setItem(this.addEnv(this.TENANT_ID), tokenInfo.tenantId);
        }
      } else {
        // 不记住我：使用 sessionStorage（会话存储，关闭浏览器后清除）
        sessionStorage.setItem(this.addEnv(this.TOKEN_KEY), tokenInfo.accessToken);
        sessionStorage.setItem(this.addEnv(this.TOKEN_INFO_KEY), JSON.stringify(tokenInfo));
        sessionStorage.setItem(this.addEnv(this.REMEMBER_ME_KEY), 'false');
        if (tokenInfo.tenantId) {
          sessionStorage.setItem(this.addEnv(this.TENANT_ID), tokenInfo.tenantId);
        }
      }
    } catch (error) {
      console.error('存储 token 失败:', error);
      throw new Error('存储 token 失败');
    }
  }

  /**
   * 获取 token
   * @returns token 字符串或 null
   */
  static getToken(): string | null {
    try {
      // 优先从 sessionStorage 获取，然后从 localStorage 获取
      let token = sessionStorage.getItem(this.addEnv(this.TOKEN_KEY));
      if (!token) {
        token = localStorage.getItem(this.addEnv(this.TOKEN_KEY));
      }
      return token;
    } catch (error) {
      console.error('获取 token 失败:', error);
      return null;
    }
  }

  /**
   * 获取完整的 token 信息
   * @returns token 信息或 null
   */
  static getTokenInfo(): TokenInfo | null {
    try {
      // 优先从 sessionStorage 获取，然后从 localStorage 获取
      let tokenInfoStr = sessionStorage.getItem(this.addEnv(this.TOKEN_INFO_KEY));
      if (!tokenInfoStr) {
        tokenInfoStr = localStorage.getItem(this.addEnv(this.TOKEN_INFO_KEY));
      }

      if (!tokenInfoStr) {
        return null;
      }

      const tokenInfo: TokenInfo = JSON.parse(tokenInfoStr);
      return tokenInfo;
    } catch (error) {
      console.error('获取 token 信息失败:', error);
      return null;
    }
  }

  /**
   * 检查 token 是否有效
   * @returns 是否有效
   */
  static isTokenValid(): boolean {
    try {
      const tokenInfo = this.getTokenInfo();
      if (!tokenInfo) {
        return false;
      }

      // 检查是否过期
      if (tokenInfo.expiresTime && Date.now() > tokenInfo.expiresTime) {
        this.clearToken();
        return false;
      }

      return true;
    } catch (error) {
      console.error('验证 token 失败:', error);
      return false;
    }
  }

  /**
   * 清除 token
   */
  static clearToken(): void {
    try {
      // 清除所有存储位置的 token
      sessionStorage.removeItem(this.addEnv(this.TOKEN_KEY));
      sessionStorage.removeItem(this.addEnv(this.TOKEN_INFO_KEY));
      sessionStorage.removeItem(this.addEnv(this.REMEMBER_ME_KEY));
      localStorage.removeItem(this.addEnv(this.TOKEN_KEY));
      localStorage.removeItem(this.addEnv(this.TOKEN_INFO_KEY));
      localStorage.removeItem(this.REMEMBER_ME_KEY);
      localStorage.removeItem(this.addEnv(this.LOGIN_URL));
    } catch (error) {
      console.error('清除 token 失败:', error);
    }
  }

  /**
   * 获取记住我状态
   * @returns 是否记住我
   */
  static getRememberMe(): boolean {
    try {
      const remembered =
        sessionStorage.getItem(this.addEnv(this.REMEMBER_ME_KEY)) ||
        localStorage.getItem(this.addEnv(this.REMEMBER_ME_KEY));
      return remembered === 'true';
    } catch (error) {
      console.error('获取记住我状态失败:', error);
      return false;
    }
  }

  /**
   * 刷新 token（如果支持的话）
   * @returns 是否刷新成功
   */
  static async refreshToken(): Promise<boolean> {
    try {
      const tokenInfo = this.getTokenInfo();
      if (!tokenInfo) {
        return false;
      }

      // TODO: 调用刷新 token 的接口
      // const response = await refreshTokenAPI();
      // if (response.success) {
      //   this.setToken(response.data, this.getRememberMe());
      //   return true;
      // }

      return false;
    } catch (error) {
      console.error('刷新 token 失败:', error);
      return false;
    }
  }

  /**
   * 获取 Authorization 头
   * @returns Authorization 头字符串
   */
  static getAuthorizationHeader(): string {
    const token = this.getToken();
    return token ? `Bearer ${token}` : '';
  }

  /**
   * 获取 tenant 信息
   * @returns tenantInfo 信息或 null
   */
  static getTenantInfo(): { tenantId: string } | null {
    try {
      // 优先从 sessionStorage 获取，然后从 localStorage 获取
      let tenantId = sessionStorage.getItem(this.addEnv(this.TENANT_ID));
      if (!tenantId) {
        tenantId = localStorage.getItem(this.addEnv(this.TENANT_ID));
      }

      if (!tenantId) return null;

      return { tenantId };
    } catch (error) {
      console.error('获取 token 失败:', error);
      return null;
    }
  }
}

/**
 * 导出默认实例
 */
export default TokenManager;

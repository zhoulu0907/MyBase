import { HttpClient } from '@onebase/common';
import {
    LoginRequest,
    LoginResponse,
    Sm2PublicKeyResponse,
} from '../types';
import { getBackendURL } from './base';

export class SessionService {
    private httpClient: HttpClient | null = null;
    private baseUrl = '/session';
    private baseURL?: string;

    constructor(baseURL?: string) {
      this.baseURL = baseURL;
    }

    /**
     * 获取或创建 HttpClient 实例
     */
    private getHttpClient(): HttpClient {
      if (!this.httpClient) {
        this.httpClient = new HttpClient({
          baseURL: this.baseURL || getBackendURL(),
          timeout: 10000,
          prefix: '/session'
        });
      }
      return this.httpClient;
    }

    /**
     * 登录接口
     * @param account 账号
     * @param password 密码
     * @param captcha_token 验证码 Token
     * @returns Promise<any>
     */
    async login(req: LoginRequest): Promise<any> {
      const client = this.getHttpClient();
      return client.post<any>(`/login`, req);
    }

    /**
     * 获取 SM2 公钥
     * @returns Promise<Sm2PublicKeyResponse>
     */
    async getSm2PublicKey(): Promise<Sm2PublicKeyResponse> {
      const client = this.getHttpClient();
      return client.get<Sm2PublicKeyResponse>(`/sm2_public_key`);
    }

  }

  /**
   * 创建会话服务实例
   */
  export const sessionService = new SessionService();

  /**
   * 导出会话相关的工具函数
   */
  export const login = (req: LoginRequest): Promise<LoginResponse> => {
    return sessionService.login(req);
  };


  /**
   * 导出账户相关的工具函数
   */
  export const getSm2PublicKey = (): Promise<Sm2PublicKeyResponse> => {
    return sessionService.getSm2PublicKey();
  };
import { HttpClient } from '@onebase/common';
import { getBackendURL } from '../base';
export class SystemService {
  private httpClient: HttpClient | null = null;
  private baseURL?: string;

  constructor(baseURL?: string) {
    this.baseURL = baseURL;
  }

  /**
   * 获取或创建 HttpClient 实例
   */
  getHttpClient(): HttpClient {
    if (!this.httpClient) {
      this.httpClient = new HttpClient({
        baseURL: this.baseURL || getBackendURL(),
        timeout: 10000,
        prefix: '/system'
      });
    }
    return this.httpClient;
  }
}

/**
 * 创建会话服务实例
 */
const systemService = new SystemService();
export default systemService.getHttpClient();
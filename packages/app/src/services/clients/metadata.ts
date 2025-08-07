import { HttpClient } from '@onebase/common';
import { getBackendURL } from '../base';
export class MetadataService {
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
        // baseURL: this.baseURL || getBackendURL(),
        baseURL: 'http://192.168.224.89:48080/admin-api',
        timeout: 10000,
        prefix: '/metadata',
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*',
          'Tenant-Id': '1'
        }
      });
    }
    return this.httpClient;
  }
}

/**
 * 创建会话服务实例
 */
const metadataService = new MetadataService();
export default metadataService.getHttpClient();

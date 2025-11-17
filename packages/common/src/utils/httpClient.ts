import { getBackendURL } from './base';
import { HttpClient } from './http';

export abstract class BaseClient {
  private httpClient: HttpClient | null = null;
  private baseURL?: string;

  constructor(baseURL?: string) {
    this.baseURL = baseURL;
  }

  /**
   * 获取 API 前缀，子类必须实现
   */
  protected abstract getPrefix(): string;

  /**
   * 获取或创建 HttpClient 实例
   */
  getHttpClient(): HttpClient {
    if (!this.httpClient) {
      this.httpClient = new HttpClient({
        baseURL: this.baseURL || getBackendURL(),
        timeout: 10000,
        prefix: this.getPrefix(),
        headers: {
          'Content-Type': 'application/json',
          'Access-Control-Allow-Origin': '*'
          //   'Tenant-Id': '1'
        }
      });
    }
    return this.httpClient;
  }
}

/**
 * 创建客户端实例的工厂函数
 * @param prefix API 前缀
 * @param baseURL 可选的 baseURL
 * @returns HttpClient 实例
 */
export function createClient(prefix: string, baseURL?: string): any {
  class DynamicClient extends BaseClient {
    protected getPrefix(): string {
      return prefix;
    }
  }

  const client = new DynamicClient(baseURL);
  return client.getHttpClient();
}

import { HttpClient } from "@onebase/common";
import { getBackendURL } from "../base";
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
        prefix: "/app",
        headers: {
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
          "Tenant-Id": "1",
        },
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

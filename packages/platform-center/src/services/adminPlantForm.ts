import { HttpClient } from '@onebase/common';
import { getBackendURL } from './base';
import { LoginRequest, LoginResponse } from '../types';

export class PlatformService { 
  private httpClient: HttpClient | null = null;
  private baseUrl = '/admin-api/system';
  private baseURL?: string;

  constructor(baseURL?: string) {
    this.baseURL = baseURL;
  }

  getHttpClient(): HttpClient {
    if (!this.httpClient) {
      this.httpClient = new HttpClient({
        baseURL: this.baseURL || getBackendURL(),
        timeout: 10000,
        prefix: this.baseUrl
      });
    }
    return this.httpClient;
  }
}

const platformService = new PlatformService();
export default platformService.getHttpClient();

export const platformLogin = async (req: LoginRequest): Promise<LoginResponse> => {
  const client = platformService.getHttpClient();
  return client.post<LoginResponse>('/auth/login', req, {
    headers: {
      'Tenant-Id': '1'
    }
  });
};
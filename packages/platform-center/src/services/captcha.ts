import { HttpClient } from '@onebase/common';
import {
    CaptchaResponse,
    CaptchaType,
    VerifyCaptchaRequest,
    VerifyCaptchaResponse
} from '../types';
import { getBackendURL } from './base';



/**
 * 验证码服务类
 * 提供验证码相关的 API 接口
 */
export class CaptchaService {
  private httpClient: HttpClient | null = null;
  private baseUrl = '/captcha';
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
      });
    }
    return this.httpClient;
  }

  /**
   * 获取验证码
   * @param captchaType 验证码类型
   * @returns Promise<CaptchaResponse>
   */
  async getCaptcha(captchaType: CaptchaType = 'WORD_IMAGE_CLICK'): Promise<CaptchaResponse> {
    const client = this.getHttpClient();
    return client.post<CaptchaResponse>(`${this.baseUrl}/get?captcha_type=${captchaType}`);
  }

  /**
   * 验证验证码
   * @param captchaId 验证码ID
   * @param captchaValue 验证码值
   * @param captchaType 验证码类型
   * @returns Promise<VerifyCaptchaResponse>
   */
  async verifyCaptcha(
    captchaId: string,
    captchaValue: string,
    captchaType: CaptchaType = 'WORD_IMAGE_CLICK'
  ): Promise<VerifyCaptchaResponse> {
    const requestData: VerifyCaptchaRequest = {
      captcha_id: captchaId,
      captcha_value: captchaValue,
      captcha_type: captchaType,
    };

    const client = this.getHttpClient();
    return client.post<VerifyCaptchaResponse>(`${this.baseUrl}/verify`, requestData);
  }

}

/**
 * 创建验证码服务实例
 */
export const captchaService = new CaptchaService();

/**
 * 导出验证码相关的工具函数
 */
export const getCaptcha = (captchaType: CaptchaType = 'WORD_IMAGE_CLICK'): Promise<CaptchaResponse> => {
  return captchaService.getCaptcha(captchaType);
};

export const verifyCaptcha = (
  captchaId: string,
  captchaValue: string,
  captchaType: CaptchaType = 'WORD_IMAGE_CLICK'
): Promise<VerifyCaptchaResponse> => {
  return captchaService.verifyCaptcha(captchaId, captchaValue, captchaType);
};
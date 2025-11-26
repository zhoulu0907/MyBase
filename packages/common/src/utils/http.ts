import { Message } from '@arco-design/web-react';
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { BaseResponse, RequestConfig, RequestInterceptor, ResponseInterceptor } from '../types';
import { getHashQueryParam } from './router';
import TokenManager from './token';

/**
 * 拼接域名和服务路径
 * @param domain 域名
 * @param path 路径
 * @returns 拼接后的完整URL
 */
export function getConcatnatedBaseUrl(domain: string, path: string = ''): string {
  const trimedDomain = domain.endsWith('/') ? domain.slice(0, -1) : domain;
  const trimedPath = path.startsWith('/') ? path.slice(1) : path;

  if (!trimedPath) {
    return trimedDomain;
  }
  return `${trimedDomain}/${trimedPath}`;
}

/**
 * HTTP 请求工具类
 * 基于 Axios 封装，提供统一的请求接口和错误处理
 */
export class HttpClient {
  private instance: AxiosInstance;
  private requestInterceptors: RequestInterceptor[] = [];
  private responseInterceptors: ResponseInterceptor[] = [];

  constructor(config: RequestConfig = {}) {
    const baseURL = getConcatnatedBaseUrl(config.baseURL || 'http://127.0.0.1:9524', config.prefix);
    this.instance = axios.create({
      baseURL,
      timeout: config.timeout || 10000,
      headers: {
        'Content-Type': 'application/json',
        ...config.headers
      },
      withCredentials: config.withCredentials || false
    });

    this.setupInterceptors();
  }

  /**
   * 设置请求和响应拦截器
   */
  private setupInterceptors(): void {
    // 请求拦截器
    this.instance.interceptors.request.use(
      (config) => {
        // 添加请求时间戳
        config.params = {
          ...config.params,
          _t: Date.now()
        };

        // 自动添加 token 到请求头
        const tokenInfo = TokenManager.getTokenInfo();
        const tenantInfo = TokenManager.getTenantInfo();
        if (tokenInfo?.accessToken) {
          config.headers['Authorization'] = `Bearer ${tokenInfo.accessToken}`;

          if (config.headers['X-Tenant-Id'] == undefined || config.headers['X-Tenant-Id'] === '') {
            config.headers['X-Tenant-Id'] = tenantInfo?.tenantId;
          }
        }

        // 执行自定义请求拦截器
        this.requestInterceptors.forEach((interceptor) => {
          if (interceptor.onFulfilled) {
            config = interceptor.onFulfilled(config);
          }
        });

        return config;
      },
      (error) => {
        // 执行自定义请求错误拦截器
        this.requestInterceptors.forEach((interceptor) => {
          if (interceptor.onRejected) {
            error = interceptor.onRejected(error);
          }
        });

        return Promise.reject(error);
      }
    );

    // 响应拦截器
    this.instance.interceptors.response.use(
      (response: AxiosResponse<BaseResponse>) => {
        // 执行自定义响应拦截器
        this.responseInterceptors.forEach((interceptor) => {
          if (interceptor.onFulfilled) {
            response = interceptor.onFulfilled(response);
          }
        });

        // 统一处理响应数据
        const { data } = response;
        if (data && typeof data === 'object') {
          if (data.code !== 0) {
            Message.error(data.msg || '请求失败');
            if (data.code === 401) {
              TokenManager.clearToken();

              const redirectURL = getHashQueryParam('redirectURL') || window.location.href;
              //   window.location.href = '/#/login';

              window.location.href = `/#/login?redirectURL=${redirectURL}`;
            }
            return Promise.reject(new Error(data.msg || '请求失败'));
          }
        }

        return response;
      },
      (error) => {
        // 执行自定义响应错误拦截器
        this.responseInterceptors.forEach((interceptor) => {
          if (interceptor.onRejected) {
            error = interceptor.onRejected(error);
          }
        });

        // 处理 token 过期
        if (error.response?.status === 401) {
          // 清除过期的 token
          TokenManager.clearToken();

          // 可以在这里触发重新登录逻辑
          // 例如：window.location.href = '/login';
        }

        // 统一错误处理
        const errorMessage = this.handleError(error);
        return Promise.reject(new Error(errorMessage));
      }
    );
  }

  /**
   * 处理错误信息
   */
  private handleError(error: any): string {
    if (error.response) {
      // 服务器返回错误状态码
      const { status, data } = error.response;
      switch (status) {
        case 400:
          return data?.message || '请求参数错误';
        case 401:
          return data?.message || '未授权，请重新登录';
        case 403:
          return data?.message || '拒绝访问';
        case 404:
          return data?.message || '请求的资源不存在';
        case 500:
          return data?.message || '服务器内部错误';
        default:
          return data?.message || `请求失败 (${status})`;
      }
    } else if (error.request) {
      // 网络错误
      return '网络连接失败，请检查网络设置';
    } else {
      // 其他错误
      return error.message || '请求失败';
    }
  }

  /**
   * 添加请求拦截器
   */
  public addRequestInterceptor(interceptor: RequestInterceptor): void {
    this.requestInterceptors.push(interceptor);
  }

  /**
   * 添加响应拦截器
   */
  public addResponseInterceptor(interceptor: ResponseInterceptor): void {
    this.responseInterceptors.push(interceptor);
  }

  /**
   * GET 请求
   */
  public async get<T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.get<BaseResponse<T>>(url, {
      params,
      ...config
    });
    return response.data.data;
  }

  /**
   * POST 请求
   */
  public async post<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.post<BaseResponse<T>>(url, data, config);
    return response.data.data;
  }

  /**
   * PUT 请求
   */
  public async put<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.put<BaseResponse<T>>(url, data, config);
    return response.data.data;
  }

  /**
   * DELETE 请求
   */
  public async delete<T = any>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.delete<BaseResponse<T>>(url, config);
    return response.data.data;
  }

  /**
   * PATCH 请求
   */
  public async patch<T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.patch<BaseResponse<T>>(url, data, config);
    return response.data.data;
  }

  /**
   * 上传文件
   */
  public async upload<T = any>(url: string, file: File, config?: AxiosRequestConfig): Promise<T> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await this.instance.post<BaseResponse<T>>(url, formData, {
      ...config,
      headers: {
        'Content-Type': 'multipart/form-data',
        ...config?.headers
      }
    });

    return response.data.data;
  }

  /**
   * 下载文件
   */
  public async download(url: string, filename?: string, config?: AxiosRequestConfig): Promise<void> {
    const response = await this.instance.get(url, {
      ...config,
      responseType: 'blob'
    });

    const blob = new Blob([response.data]);
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = downloadUrl;
    link.download = filename || 'download';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(downloadUrl);
  }

  /**
   * 获取 Axios 实例（用于高级用法）
   */
  public getInstance(): AxiosInstance {
    return this.instance;
  }
}

/**
 * 创建默认的 HTTP 客户端实例
 */
export const httpClient = new HttpClient();

/**
 * 导出常用的请求方法
 */
export const { get, post, put, delete: del, patch, upload, download } = httpClient;

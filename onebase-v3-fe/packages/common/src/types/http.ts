// 基础响应类型
export interface BaseResponse<T = any> {
  code: number;
  msg: string;
  data: T;
}

// HTTP 请求配置
export interface RequestConfig {
  baseURL?: string;
  timeout?: number;
  headers?: Record<string, string>;
  withCredentials?: boolean;
  prefix?: string;
}

// 请求拦截器
export interface RequestInterceptor {
  onFulfilled?: (config: any) => any;
  onRejected?: (error: any) => any;
}

// 响应拦截器
export interface ResponseInterceptor {
  onFulfilled?: (response: any) => any;
  onRejected?: (error: any) => any;
}

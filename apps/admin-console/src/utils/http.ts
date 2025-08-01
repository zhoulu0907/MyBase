import { TokenManager } from '@onebase/common';
import axios,  {type AxiosInstance, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios';

// 定义通用的响应结构
export interface HttpResponse<T = any> {
  code: number;
  data: T;
  msg: string;
}
// 创建 axios 实例
const http: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 默认基础URL
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 可以在这里添加认证 token
    // const token = localStorage.getItem('onebase_token');
    const tokenInfo = TokenManager.getTokenInfo();
    console.log('token:', tokenInfo);
    const token = tokenInfo?.tokenValue;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
http.interceptors.response.use(
  (response) => {
    const { code, msg } = response.data;
    // 根据后端约定的状态码进行处理
    if (code === 0) {
      return response.data;
    } else if (code === 401) {
      // 登录过期，需要重新登录
      TokenManager.clearToken()
      console.error('登录过期，请重新登录');
      window.location.href = '/login';
      return Promise.reject(new Error(msg || '登录过期，请重新登录'));
    } else {
      // 可以根据需要处理不同的错误码
      console.error(`请求错误: ${msg}`);
      return Promise.reject(new Error(msg || '请求失败'));
    }
  },
  (error) => {
    console.error('网络错误:', error);
    return Promise.reject(error);
  }
);

// 封装常用的请求方法
export const httpGet = <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
  return http.get(url, config);
};

export const httpPost = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
  return http.post(url, data, config);
};

export const httpPut = <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> => {
  return http.put(url, data, config);
};

export const httpDelete = <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> => {
  return http.delete(url, config);
};

export default http;
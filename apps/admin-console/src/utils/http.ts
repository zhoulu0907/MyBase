import axios,  {type AxiosInstance, type AxiosRequestConfig, type AxiosResponse, type InternalAxiosRequestConfig } from 'axios';

// 定义通用的响应结构
export interface HttpResponse<T = any> {
  code: number;
  data: T;
  message: string;
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
    const token = localStorage.getItem('access_token');
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
  (response: AxiosResponse<HttpResponse>) => {
    const { code, data, message } = response.data;
    
    // 根据后端约定的状态码进行处理
    if (code === 200) {
      return data;
    } else {
      // 可以根据需要处理不同的错误码
      console.error(`请求错误: ${message}`);
      return Promise.reject(new Error(message || '请求失败'));
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
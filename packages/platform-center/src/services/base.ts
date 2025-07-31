
// 扩展 Window 接口
declare global {
    interface Window {
      __ENV__?: {
        API_BASE_URL?: string;
      };
    }
}

/**
 * 获取后端服务地址
 * 优先使用环境变量，其次使用默认值
 */
export const getBackendURL = (): string => {
    // 支持环境变量配置
    if (typeof window !== 'undefined' && window.__ENV__?.API_BASE_URL) {
      return window.__ENV__.API_BASE_URL;
    }

    // 支持 Vite 环境变量
    const metaEnv = (import.meta as any)?.env;
    if (metaEnv?.VITE_API_BASE_URL) {
      return metaEnv.VITE_API_BASE_URL;
    }

    // 默认后端地址
    return 'http://localhost:9524';
};

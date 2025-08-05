
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
    const baseUrl = (window as any).global_config.BASE_URL
    return baseUrl || 'http://localhost:9524';
};

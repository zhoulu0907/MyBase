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
  const baseUrl = (window as any).global_config?.BASE_URL;
  return baseUrl || 'http://localhost:9524';
};

export const getPlatformBackendURL = (): string => {
  const baseUrl = (window as any).global_config?.PLATFORM_BASE_URL;
  return baseUrl || 'http://localhost:9524';
};

export const getPlatformFeURL = (): string => {
  const baseUrl = (window as any).global_config?.PLATFORM_FE_URL;
  return baseUrl || 'http://localhost:4399';
};

export const getRuntimeBackendURL = (): string => {
  const baseUrl = (window as any).global_config?.RUNTIME_BASE_URL;
  return baseUrl || 'http://localhost:9524';
};

export const getRuntimeURL = (): string => {
  const runtimeUrl = (window as any).global_config?.RUNTIME_URL;
  return runtimeUrl || 'http://localhost:9527';
};

export const getRuntimeMobileURL = (): string => {
  const runtimeMobileUrl = (window as any).global_config?.RUNTIME_MOBILE_URL;
  return runtimeMobileUrl || 'http://localhost:9527';
};
export const getDashbordDataSetUrl = (): string => {
  const dashbordDataSetUrl = (window as any).global_config?.APP_BUILDER_DATASET_URL;
  return dashbordDataSetUrl || 'http://localhost:9528';
};

import { CONFIG_PRIVATE_KEY, sm2Decrypt } from './crypto';

export const getProdConfig = () => {
  const config = (window as any).global_config?.CONFIG;
  if (config) {
    const decryptedData = sm2Decrypt(CONFIG_PRIVATE_KEY, config);
    return JSON.parse(decryptedData as string);
  }

  return null;
};

export const envConfig = process.env.NODE_ENV === 'production' ? getProdConfig() : (window as any).global_config;

// 平台、空间、应用端环境
export const getEnv = (): string => {
  const environment = envConfig?.ENVIRONMENT;
  return environment;
};

export const isPlatformEnv = (): boolean => {
  return getEnv() === 'platform';
};

export const isBuilderEnv = (): boolean => {
  return getEnv() === 'builder';
};

export const isRuntimeEnv = (): boolean => {
  return getEnv() === 'runtime';
};

export const getSignatureConfig = (): { appKey: string; appSecret: string } => {
  const appKey = envConfig?.APP_KEY;
  const appSecret = envConfig?.APP_SECRET;
  return { appKey, appSecret };
};

export const getPublicKey = (): string => {
  return envConfig?.PUBLIC_KEY;
};

export const getMobileEditorURL = (): string => {
  return envConfig?.MOBILE_EDITOR_URL;
};

export const getResourceURL = (): string => {
  return envConfig?.RESOURCE_URL;
};
export const getDashBoardURL = (): string => {
  return envConfig?.APP_BUILDER_DASHBOARD_URL;
};

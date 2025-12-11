export const getEnv = (): string => {
  const environment = (window as any).global_config?.ENVIRONMENT;
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
  const appKey = (window as any).global_config?.APP_KEY;
  const appSecret = (window as any).global_config?.APP_SECRET;
  return { appKey, appSecret };
};

export const getPublicKey = (): string => {
  return (window as any).global_config?.PUBLIC_KEY;
};

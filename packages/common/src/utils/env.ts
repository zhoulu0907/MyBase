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

export const getEnv = (): string => {
  const environment = (window as any).global_config?.ENVIRONMENT;
  return environment;
};

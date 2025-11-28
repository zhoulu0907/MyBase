import { createClient, getPlatformBackendURL, getRuntimeBackendURL } from '@onebase/common';

/**
 * 预定义的客户端实例
 */
export const systemService = createClient('/system');
export const platformService = createClient('/system', getPlatformBackendURL());
export const infraService = createClient('/infra');
export const runtimeInfraService = createClient('/infra', getRuntimeBackendURL());
export const platformInfraService = createClient('/infra', getPlatformBackendURL());
export const appService = createClient('/app');
export const corpService = createClient('/corp');

export const runtimeService = createClient('/system', getRuntimeBackendURL());

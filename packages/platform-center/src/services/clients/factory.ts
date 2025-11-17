import { createClient, getPlatformBackendURL, getRuntimeBackendURL } from '@onebase/common';

/**
 * 预定义的客户端实例
 */
export const systemService = createClient('/system');
export const platformService = createClient('/system', getPlatformBackendURL());
export const runtimeService = createClient('/system', getRuntimeBackendURL());
export const infraService = createClient('/infra');
export const appService = createClient('/app');

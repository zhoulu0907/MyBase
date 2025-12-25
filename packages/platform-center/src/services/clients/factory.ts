import { createClient, getPlatformBackendURL, getRuntimeBackendURL, getDashboardDataSetUrl } from '@onebase/common';

/**
 * 预定义的客户端实例
 */
export const systemService = createClient('/system');
export const platformService = createClient('/system', getPlatformBackendURL());
export const infraService = createClient('/infra');
export const userService = createClient('/user');
export const platformInfraService = createClient('/infra', getPlatformBackendURL());
export const appService = createClient('/app');
export const dashboardService = createClient('/dashboard')

export const runtimeService = createClient('/system', getRuntimeBackendURL());
export const runtimeInfraService = createClient('/infra', getRuntimeBackendURL());
export const runtimeCorpService = createClient('/corp', getRuntimeBackendURL());
export const dashboardDataSetService = createClient('/de2api', getDashboardDataSetUrl());
export const runtimeUserService = createClient('/user', getRuntimeBackendURL());

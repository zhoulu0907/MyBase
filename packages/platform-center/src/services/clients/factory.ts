import { createClient } from '@onebase/common';

/**
 * 预定义的客户端实例
 */
export const systemService = createClient('/system');
export const infraService = createClient('/infra');

import { createClient } from '@onebase/common';


/**
 * 预定义的客户端实例
 */
export const appService = createClient('/app');
export const metadataService = createClient('/metadata');

import { createClient, getRuntimeBackendURL } from '@onebase/common';

/**
 * 预定义的客户端实例
 */
export const appService = createClient('/app');
export const metadataService = createClient('/metadata');
export const runtimeMetadataService = createClient('/metadata', getRuntimeBackendURL());
export const runtimeListdataService = createClient('/bpm', getRuntimeBackendURL());
export const runtimeFlowService = createClient('/flow', getRuntimeBackendURL());
export const flowService = createClient('/flow');
export const formulaService = createClient('/formula');
export const bpmService = createClient('/bpm');
export const etlService = createClient('/etl');

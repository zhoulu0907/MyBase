import { runtimeFlowService } from './clients';

// 查询页面触发列表 
export const queryFlowExecForm = (pageId:string) => {
  return runtimeFlowService.get(`/exec/form/query?pageId=${pageId}`);
};

// 触发页面
export const triggerFlowExecForm = (params: any) => {
  return runtimeFlowService.post('/exec/form/trigger', params);
};
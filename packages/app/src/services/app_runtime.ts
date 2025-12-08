import { isRuntimeEnv } from '@onebase/common';
import {
  FetchExecTaskReq,
  GetDonePageList,
  GetFormDetailReq,
  GetListNodesReq,
  GetMyCCPageListReq,
  GetMyCreatePageListReq,
  GetOperatorRecord,
  GetPageSetListReq,
  GetRunTimePageSetIdReq,
  SubMitInstanceReq
} from '../types';
import { appService, runtimeAppService, runtimeListdataService } from './clients';

export const getTodoPageList = (params: GetRunTimePageSetIdReq) => {
  return runtimeListdataService.get('/task-center/todo/page', params);
};
export const getDonePageList = (params: GetDonePageList) => {
  return runtimeListdataService.get('/task-center/done/page', params);
};
export const getMyCreatePageList = (params: GetMyCreatePageListReq) => {
  return runtimeListdataService.get('/task-center/my-create/page', params);
};
export const fetchExecTask = (params: FetchExecTaskReq) => {
  return runtimeListdataService.post('/instance/exec-task', params);
};
export const getFormDetail = (params: GetFormDetailReq) => {
  return runtimeListdataService.get('/instance/get-form-detail', params);
};
export const getOperatorRecord = (params: GetOperatorRecord) => {
  return runtimeListdataService.get('/instance/get-operator-record', params);
};
export const fetchSubmitInstance = (params: SubMitInstanceReq) => {
  return runtimeListdataService.post('/instance/submit', params);
};
export const fetchFlowPredict = (params: any) => {
  return runtimeListdataService.post('/instance/flow-predict', params);
};

export const getPageSetList = (params: GetPageSetListReq) => {
  return (isRuntimeEnv() ? runtimeAppService : appService).get('/resource/page_set/list', params);
};
export const getListNodes = (params: GetListNodesReq) => {
  return runtimeListdataService.get('/task-center/list-nodes', params);
};
export const getMyCCPageList = (params: GetMyCCPageListReq) => {
  return runtimeListdataService.get('/task-center/cc/page', params);
};

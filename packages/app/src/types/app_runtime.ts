export interface GetRunTimePageSetIdReq {
  pageNo?: number;
  pageSize?: number;
  processTitle?: string;
  initiator?: string;
  formSummary?: string;
  sortType?: string;
  appId: string;
  submitTimeStart?: string;
  submitTimeEnd?: string;
}
export interface FetchExecTaskReq {
  buttonType: string;
  comment: string;
  taskId: string;
  instanceId: string;
  entity?: any;
}
export interface GetFormDetailReq {
  taskId?: string;
  instanceId?: string;
}
export interface GetDonePageList {
  appId?: string;
  handleTime?: string;
  handleTimeEnd?: string;
  handleTimeStart?: string;
  initiator?: string;
  pageNo?: string;
  pageSize?: string[];
  processTitle?: string;
  sortType?: string;
}
export interface GetOperatorRecord {
  instanceId: string;
}


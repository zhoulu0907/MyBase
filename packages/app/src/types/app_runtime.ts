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
export interface SubMitInstanceReq {
  isDraft?: boolean;
  formName: string;
  businessId: string;
  entity: any;
}

export enum FLOWSTATUS_TYPE {
  IN_APPROVAL = 'in_approval',
  DRAFT = 'draft',
  APPROVED = 'approved',
  REJECTED = 'rejected',
  WITHDRAWN = 'withdrawn',
  TERMINATED = 'terminated'
}

export const FlowStatusMap = {
  [FLOWSTATUS_TYPE.IN_APPROVAL]: '审批中',
  [FLOWSTATUS_TYPE.DRAFT]: '草稿',
  [FLOWSTATUS_TYPE.APPROVED]: '已通过',
  [FLOWSTATUS_TYPE.REJECTED]: '已拒绝',
  [FLOWSTATUS_TYPE.WITHDRAWN]: '已撤回',
  [FLOWSTATUS_TYPE.TERMINATED]: '已终止'
};

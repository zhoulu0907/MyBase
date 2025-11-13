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
export interface GetMyCreatePageListReq {
  pageNo?: number;
  pageSize?: number;
  processTitle?: string;
  initiator?: string;
  formSummary?: string;
  sortType?: string;
  appId: string;
  submitTimeStart?: string;
  submitTimeEnd?: string;
  flowStatus?:string;
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

export const TaskStatusMap = {
  SUBMITTED: '已提交',
  AGREED: '已同意',
  PASS: '自动通过',
  REJECTED: '已拒绝',
  RETURNED: '已退回',
  WITHDRAWN: '已撤回',
  AUTOREJECTED: '自动拒绝'
};

export enum TASKMENU_TYPE {
  TASKINEEDTODO = 'TASK-ineedtodo',
  TASKIHAVEDONE = 'TASK-ihavedone',
  TASKICREATED = 'TASK-icreated',
  TASKICOPIED = 'TASK-icopied',
  TASKTASKPROXY = 'TASK-taskproxy',
}

export enum LISTTYPE {
  WILLDO = 'willdo',
  IDONE = 'idone',
  ICREATED = 'icreated'
}
export const BPMConfigButtonType = {
  APPROVE: 'approve',
  REJECT: 'reject',
  SAVE: 'save',
  TRANSFER: 'transfer',
  ADD_SIGN: 'add_sign',
  RETURN: 'return',
  WITHDRAW: 'withdraw',
  ABSTAIN: 'abstain',
  SUBMIT: 'submit'
};

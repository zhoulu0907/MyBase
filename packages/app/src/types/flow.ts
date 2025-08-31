export interface FlowMgmt {
  id: string;
  applicationId: string;
  processName: string;
  processStatus: ProcessStatus;
  processDefinition: string;
  processDescription: string;
  triggerType: string;
  createTime: string;
  updateTime: string;
}

export enum ProcessStatus {
  DISABLED = 0,
  ENABLED = 1
}

export enum ProcessDefinition {
  Time = 'time',
  FORM = 'form',
  DATE_FIELD = 'date_field',
  ENTITY = 'entity',
  API = 'api'
}

export interface ListFlowMgmtReq {
  pageNum: number;
  pageSize: number;

  applicationId: string;
  processName?: string;
  processStatus?: ProcessStatus;
  triggerType?: string;
}

export interface CreateFlowMgmtReq {
  applicationId: string;
  processName: string;
  processStatus: ProcessStatus;
  processDescription?: string;
  processDefinition: ProcessDefinition;
  triggerType: string;
}

export interface UpdateFlowMgmtReq {
  id: string;
  applicationId: string;
  processName: string;
  processStatus: ProcessStatus;
  processDescription?: string;
  processDefinition: ProcessDefinition;
  triggerType: string;
}

export interface RenameFlowMgmtReq {
  id: string;
  processName: string;
}

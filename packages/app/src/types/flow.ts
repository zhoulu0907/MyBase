export interface FlowMgmt {
  id: string;
  applicationId: string;
  processName: string;
  processStatus: ProcessStatus;
  processDescription: string;
  triggerType: string;
  createTime: string;
  updateTime: string;
}

export enum ProcessStatus {
  DISABLED = 0,
  ENABLED = 1
}

export enum TriggerType {
  FORM = 'form',
  ENTITY = 'entity',
  TIME = 'time',
  DATE_FIELD = 'date_field',
  API = 'api',
  BPM = 'bpm'
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
  triggerType: string;
}

export interface UpdateFlowMgmtReq {
  id: string;
  applicationId: string;
  processName: string;
  processStatus: ProcessStatus;
  processDescription?: string;
  triggerType: TriggerType;
}

export interface RenameFlowMgmtReq {
  id: string;
  processName: string;
}

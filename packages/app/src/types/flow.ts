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
  ORIGINAL = -1,
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

export interface TriggerConfig {
  pageId?: string;
  entityId?: string;
}

export interface CreateFlowMgmtReq {
  applicationId: string;
  processName: string;
  processStatus: ProcessStatus;
  processDescription?: string;
  triggerType: string;
  triggerConfig?: TriggerConfig;
}

export interface UpdateFlowMgmtReq {
  id: string;
  applicationId: string;
  processName: string;
  processStatus: ProcessStatus;
  processDescription?: string;
  triggerType: TriggerType;
  triggerConfig?: TriggerConfig;
}

export interface RenameFlowMgmtReq {
  id: string;
  processName: string;
}

export interface UpdateFlowMgmtDefinitionReq {
  id: string | undefined;
  processDefinition: string;
  processStatus: number | string;
}

// 数据源类型   表单、数据节点、关联表、子表
export enum DATA_SOURCE_TYPE {
  FORM = 'mainEntity', // 主表
  SUBFORM = 'subEntity', // 子表
  DATA_NODE = 'dataNode', // 数据节点
  ASSOCIA_FORM = 'associaForm', // 关联表
  LOOP = 'loop' // 循环体
}

// 查询规则  全部数据、按条件过滤
export enum FILTER_TYPE {
  ALL = 'all',
  CONDITION = 'condition'
}

export interface SelectOption {
  label: string;
  value: string;
}

export enum CAL_TYPE {
  FORMULA = 'FORMULA',
  DATASUMMARY = 'DATASUMMARY'
}

// 触发事件
export enum TRIGGER_EVENTS {
  CREATE = 'create',
  UPDATE = 'update',
  DELETE = 'delete'
}
export interface FlowMgmt {
  id: string;
  applicationId: string;
  processName: string;
  enableStatus: ProcessStatus;
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
  enableStatus: ProcessStatus;
  processDescription?: string;
  triggerType: string;
  triggerConfig?: TriggerConfig;
}

export interface UpdateFlowMgmtReq {
  id: string;
  applicationId: string;
  processName: string;
  enableStatus: ProcessStatus;
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
  enableStatus: number | string;
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

// 跳转节点 目标页面类型
export enum TARGET_PAGE_TYPE {
  INSIDE = 'inside', // 系统内页面
  OUTSIDE = 'outside' // 外部链接
}

// 打开方式
export enum OPEN_PAGE_TYPE {
  CURRENT_WINDOW = 'currentWindow', // 当前窗口覆盖
  NEW_WINDOW = 'newWindow', // 新窗口打开
  MODAL = 'MODAL', // 弹窗打开
  DRAWER = 'drawer' // 侧边栏打开
}

// 弹窗尺寸
export enum MODAL_SIZE_TYPE {
  SMALL = 'small',
  MEDIUN = 'medium',
  LARGE = 'large',
  CUSTOM = 'custom'
}

// 无权限时
export enum UNAUTHORIZED_EVENT {
  PROMPT = 'prompt',
  JUMP = 'jump'
}

//  刷新节点 刷新范围
export enum REFRESH_TYPE {
  CURRENT_PAGE = 'currentPage', // 当前页面
  SPECIFY_PAGE = 'specifyPage', // 指定页面
  SPECIFY_COMPONENT = 'specifyComponent' // 页面内指定组件
}
// 刷新策略
export enum REFRESH_STRATEGY {
  RESERVE = 'reserve', // 保留状态刷新
  RESET = 'reset' // 重置状态刷新
}

export interface ListConnectFlowNodeReq {
  pageNo: number;
  pageSize: number;
  typeName?: string;
  level1Code?: string;
  level2Code?: string;
  level3Code?: string;
}

export interface ConnectFlowNodeCategory {
  code: string;
  name: string;
}
export interface ConnectFlowNode {
  level1Code: string;
  level2Code?: string;
  level3Code?: string;
  typeCode: string;
  typeName: string;
}

export interface ListConnectInstanceReq {
  pageNo: number;
  pageSize: number;

  applicationId?: string;
  connectorName?: string;
  level1Code?: string;
  level2Code?: string;
  level3Code?: string;
}

export interface CreateConnectInstanceReq {
  applicationId: string;
  connectionName: string;
  description?: string;
  typeCode: string;
  config?: any;
}

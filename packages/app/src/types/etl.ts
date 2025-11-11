export enum ETL_SCHEDULE_STRATEGY {
  ALL = 'all', // 全部类型
  FIXED = 'fixed', // 定时更新
  OBSERVE = 'observe', // 观察更新
  MANUALLY = 'manually' // 手动更新
}

export enum ETL_FLOW_STATUS {
  ORIGINAL = -1,
  DISABLED = 0,
  ENABLED = 1
}

export enum IS_SYNC_DONE {
  NO = 0,
  YES = 1
}

export interface ETLFlowMgmt {
  id: string; // 流程ID
  applicationId: string; // 应用ID
  flowName: string; // 流程名称
  enableStatus: ETL_FLOW_STATUS; // 流程状态
  scheduleStrategy: ETL_SCHEDULE_STRATEGY; // 调度策略
  sourceTables: string[]; // 输入源
  targetTable: string; // 输出源

  isSyncDone: IS_SYNC_DONE; // 是否同步完成

  lastSuccessTime: string; // 最后成功时间
}

export interface PingDatasourceReq {
  datasourceType: string;
  config: ETLDatasourceConfig;
}

export interface ETLDatasourceConfig {
  host: string;
  port: number;
  database: string;
  username: string;
  password: string;
  connectMode?: string;
}

export interface CreateDataSourceReq {
  datasourceCode?: string;
  datasourceName: string;
  datasourceType: string;
  config: ETLDatasourceConfig;
  applicationId: string;
  declaration?: string;
  readonly: number;
  withCollect?: number;
}

export interface UpdateDataSourceReq extends CreateDataSourceReq {
  id: string;
}

export interface PageDatasourceReq {
  applicationId: string;
  datasourceCode: string;
  datasourceName: string;
  datasourceType: string;
  readonly: number;
  collectStatus: string;
  pageNo: number;
  pageSize: number;
}

export interface PreviewDatasourceReq {
  datasourceId: string;
  tableId: string;
}

export interface ListTablesReq {
  id: string;
  writable?: number;
}

export interface ETLTable {
  id: string;
  name: string;
}

export interface ELTColumn {
  fieldFqn: string;
  fieldName: string;
  fieldType: string;
}

export interface ETLDatasourceOption {
  id: string;
  name: string;
}

export interface ListAppETLDatasourceParams {
  applicationId: string;
  writable: number;
}
export interface ListTableColumnsReq {
  tableId: string;
}

export interface PageETLFlowReq {
  flowName?: string;
  scheduleStrategy?: string;
  enableStatus?: number;
  pageNo: number;
  pageSize: number;
}

export interface CreateETLFlowReq {
  applicationId: string;
  flowName: string;
  config: any;
}

export interface UpdateETLFlowReq {
  id: string;
  applicationId: string;
  flowName: string;
  config: any;
}

export interface DeleteETLFlowReq {
  id: string;
}

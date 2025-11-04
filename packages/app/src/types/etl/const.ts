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

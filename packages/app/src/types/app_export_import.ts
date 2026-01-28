export enum ExportStatus {
  UNKNOWN = 0,
  EXPORTING = 1,
  SUCCESS = 2,
  ERROR = 3
}
export interface AppExportRecord {
    id: string;
    // 操作人
    operator: string;
    // 操作时间
    operateTime: string;
    // 状态
    status: string;
}
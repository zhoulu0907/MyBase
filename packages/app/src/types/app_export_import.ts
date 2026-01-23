export enum ExportStatus {
  EXPORTING = 'exporting',
  SUCCESS = 'success',
  ERROR = 'error'
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
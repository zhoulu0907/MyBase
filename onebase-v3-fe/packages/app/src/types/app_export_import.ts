export enum ExportStatus {
  UNKNOWN = 0,
  EXPORTING = 1,
  SUCCESS = 2,
  ERROR = 3
}
export interface AppExportRecord {
    id: string;
    // 操作人
    creatorName: string;
    // 操作时间
    createTime: string;
    // 状态
    exportStatus: string;
}
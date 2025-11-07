export interface VersionType {
  createTime: number;
  creator: Creator;
  id: string;
  updater: Updater;
  updateTime: number;
  version: string;
  versionAlias: string;
  versionStatus: string;
  [property: string]: any;
}

export interface Creator {
  operationName: string;
  operationUserAvatar: string;
  operationUserId: string;
  [property: string]: any;
}

export interface Updater {
  operationName: string;
  operationUserAvatar: string;
  operationUserId: string;
  [property: string]: any;
}

// 枚举
export enum VersionStatus {
  PUBLISHED = '已发布',
  DESIGNING = '设计中',
  MANAGE= 'manage',
}

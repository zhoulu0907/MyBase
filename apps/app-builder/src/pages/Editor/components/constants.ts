

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

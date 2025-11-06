export enum VersionStatus {
  ALL = 'all',
  PUBLISHED = 'published',
  DESIGNING = 'designing',
  HISTORY = 'previous'
}

export enum SortType {
  UPDATE_TIME = 'update_time',
  CREATE_TIME = 'create_time'
}

export interface VersionModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
}
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

export interface VersionDataCreator {
  userId: string;
  name: string;
  avatar: string;
}

export interface VersionData {
  id: string;
  version: string;
  versionAlias: string;
  versionStatus: string;
  creator: VersionDataCreator;
  createTime: string;
  updateTime: string;
  updater?: VersionDataCreator;
}

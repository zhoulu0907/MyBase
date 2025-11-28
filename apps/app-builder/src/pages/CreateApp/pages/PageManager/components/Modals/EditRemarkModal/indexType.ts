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

export interface EditRemarkModal {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  currentItem: VersionData;
  getVersionMgmtData: () => void;
  getVersonList: () => void;
}

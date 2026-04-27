export interface VersionDataCreator {
  userId: string;
  name: string;
  avatar: string;
}

export interface VersionData {
  id: string;
  bpmVersion: string;
  bpmVersionAlias: string;
  bpmVersionStatus: string;
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

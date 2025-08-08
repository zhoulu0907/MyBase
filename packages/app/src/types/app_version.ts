export interface ListApplicationVersionReq {
  applicationId: number;
}

export interface CreateApplicationVersionReq {
  applicationId: number;
  versionName: string;
  versionNumber: string;
}

export interface RestoreApplicationVersionReq {
  applicationId: number;
  versionId: number;
}

export interface DeleteApplicationVersionReq {
  applicationId: number;
  versionId: number;
}

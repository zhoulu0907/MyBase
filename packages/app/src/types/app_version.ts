export interface ListApplicationVersionReq {
  applicationId: string;
}

export interface CreateApplicationVersionReq {
  applicationId: string;
  versionName: string;
  versionNumber: string;
}

export interface RestoreApplicationVersionReq {
  applicationId: string;
  versionId: number;
}

export interface DeleteApplicationVersionReq {
  applicationId: string;
  versionId: number;
}

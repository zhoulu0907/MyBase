/**
 * 流程设计
 */
export interface GetByBusinessId {
    businessId: string;
  }
export interface SaveRequest {
    bpmDefJson: string;
    businessId: string;
    flowCode: string;
    flowName: string;
    version: string;
    versionAlias: string;
    versionStatus: string;
    xid: string;
}
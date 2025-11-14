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
  id: string;
}
export interface PublishRequest {
  id: string;
}
export interface UpdateVersionAlias {
   id: string;
   versionAlias:string
}
export interface VersionMgmtRequest {
    /**
     * pageSetId
     */
    businessId?: string;
    /**
     * 页码
     */
    pageNo?: string;
    /**
     * 每页条数
     */
    pageSize?: string;
    /**
     * 排序方式：update_time-按更新时间排序, create_time-按创建时间排序
     */
    sortType?: string;
    /**
     * 流程版本备注(版本备注或者版本号)
     */
    versionAlias?: string;
    /**
     * 版本流程状态：all-全部, published-已发布, designing-设计中, previous-历史
     */
    versionStatus?: string;
}
import { BaseNode, Condition } from '../base';
import { SortType } from '../sort';

// 获取数据节点
export interface GetDataNode extends BaseNode {
  payload: GetDataNodePayload;
}

export enum GetDataType {
  // 表单
  FORM = 'form',
  // 子表单
  SUB_FORM = 'sub_form',
  // 接口
  API = 'api'
}

export interface GetDataNodePayload {
  // 目标表单
  pageId: string;
  getDataType: GetDataType;
  condition?: Condition;
  // 返回的字段id列表,为空返回全部
  fieldIds?: string[];
  // 排序类型
  sortType: SortType;
  // 排序字段
  sortField?: string;
  // 分页大小
  pageSize?: number;
  // 分页页码
  pageNum?: number;
}

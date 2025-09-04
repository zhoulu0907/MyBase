import { BaseNode, Condition } from './base';

// 触发节点
export interface TriggerNode extends BaseNode {
  payload: FromTriggerNodePayload;
}

// 表单事件触发
export interface FromTriggerNodePayload {
  // 表单页面id
  pageId: string;
  // 触发事件: 创建成功、编辑成功、删除成功
  triggerEvent: string[];
  // 触发方式: 允许自动触发
  triggerType?: string;
  // 过滤条件
  condition?: Condition;
}

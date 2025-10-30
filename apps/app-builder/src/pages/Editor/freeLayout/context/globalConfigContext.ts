import React from 'react';
export interface GlobalConfigData {
  // 允许节点自定义
  allowNodeCustomization: boolean;
  // 自动审批配置
  autoApproval: string[];
  // 审批人为空时的处理方式
  emptyApprover: string;
  permission: string; // 撤回权限
  timing: string; // 撤回时机
  // 流程退回规则
  returnRules: string;
  // 发起人终止流程权限
  terminatePermission: string;
  // 表单摘要字段
  summaryFields: string[];
}

// 全局设置参数
const data: GlobalConfigData = {
  // 允许节点自定义
  allowNodeCustomization: true,
  // 自动审批配置
  autoApproval: ['Beijing'],
  // 审批人为空时的处理方式
  emptyApprover: 'a',
  permission: 'a', // 撤回权限
  timing: 'a', // 撤回时机
  // 流程退回规则
  returnRules: 'a',
  // 发起人终止流程权限
  terminatePermission: 'a',
  // 表单摘要字段
  summaryFields: ['Beijing', 'Shenzhen', 'Wuhan']
};

export interface GlobalConfigContextType {
  configData: GlobalConfigData;
  setConfigData: (node: any) => void;
}
export const GlobalConfigContext = React.createContext<GlobalConfigContextType>({
  configData: data,
  setConfigData: () => {}
});

import { useState } from 'react';

import { GlobalConfigContext, type GlobalConfigData } from '../../../context';

export function GlobalConfigProvider({ children }: { children: React.ReactNode }) {
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
  const [configData, setConfigData] = useState<GlobalConfigData>(data);

  return <GlobalConfigContext.Provider value={{ configData, setConfigData }}>{children}</GlobalConfigContext.Provider>;
}

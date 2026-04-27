import { useState } from 'react';

import { GlobalConfigContext, type GlobalConfigData } from '../../../context';

export function GlobalConfigProvider({ children }: { children: React.ReactNode }) {
  // 全局设置参数
  const data: GlobalConfigData = {
    useNodeConfig: false,
    autoApproveCfg: {
      initAutoApprove: false,
      dupUserAutoApprove: false,
      prevNodeDupUserAutoApprove: false
    },
    emptyApproverCfg: {
      handlerMode: 'pause',
      transferMemberId: ''
    },
    withdrawRuleCfg: {
      permission: 'none',
      timing: 'unprocessed'
    },
    returnRuleCfg: {
      rule: 'seq'
    },
    initiatorTerminateCfg: {
      permission: 'initiation_node'
    },
    formSummaryCfg: {
      fieldConfigs: []
    }
  };
  const [configData, setConfigData] = useState<GlobalConfigData>(data);

  return <GlobalConfigContext.Provider value={{ configData, setConfigData }}>{children}</GlobalConfigContext.Provider>;
}

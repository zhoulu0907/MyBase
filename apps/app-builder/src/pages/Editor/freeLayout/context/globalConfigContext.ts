import React from 'react';
export interface FieldConfig {
  fieldId: string;
  fieldName: string;
}

export interface AutoApproveCfg {
  initAutoApprove: boolean;
  dupUserAutoApprove: boolean;
  prevNodeDupUserAutoApprove: boolean;
}

export interface EmptyApproverCfg {
  handlerMode: 'pause' | 'skip' | 'transfer_admin' | 'transfer_member';
  transferMemberId: string;
}

export interface WithdrawRuleCfg {
  permission: 'none' | 'initiation_node' | 'any';
  timing: 'unprocessed' | 'unread';
}

export interface ReturnRuleCfg {
  rule: 'seq' | 'direct';
}

export interface InitiatorTerminateCfg {
  permission: 'initiation_node' | 'any' | 'none';
}

export interface FormSummaryCfg {
  fieldConfigs: FieldConfig[];
}

export interface GlobalConfigData {
  useNodeConfig: boolean;
  autoApproveCfg: AutoApproveCfg;
  emptyApproverCfg: EmptyApproverCfg;
  withdrawRuleCfg: WithdrawRuleCfg;
  returnRuleCfg: ReturnRuleCfg;
  initiatorTerminateCfg: InitiatorTerminateCfg;
  formSummaryCfg: FormSummaryCfg;
}

export interface GlobalConfigContextType {
  configData: GlobalConfigData;
  setConfigData: (node: any) => void;
}
export const GlobalConfigContext = React.createContext<GlobalConfigContextType>({
  configData: {} as GlobalConfigData,
  setConfigData: () => {}
});

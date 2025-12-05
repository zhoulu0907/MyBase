import { create } from 'zustand';
import { type FreeLayoutPluginContext } from '@flowgram.ai/free-layout-editor';
export interface FlowData {
  id?: string;
  flowCode?: string;
  flowName?: string;
  bpmVersion?: string;
  bpmVersionAlias?: string;
  bpmVersionStatus?: string;
  businessUuid?: string;
}

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

export interface FlowEditorState {
  businessId: string;
  setBusinessId: (businessId: string) => void;
  currentFlowId: string;
  setCurrnetFlowId: (versionId: string) => void;
  // 自由布局实例
  editorRef: FreeLayoutPluginContext | null;
  setEditorRef: (editorRef: FreeLayoutPluginContext | null) => void;
  flowData: FlowData;
  setFlowData: (flowData: FlowData) => void;
  configData: GlobalConfigData;
  setConfigData: (node: any) => void;
  initFlowData: () => void;
}
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
export const useFlowEditorStor = create<FlowEditorState>((set) => ({
  businessId: '',
  setBusinessId: (businessId: string) => set(() => ({ businessId })),
  currentFlowId: '',
  setCurrnetFlowId: (currentFlowId: string) => set(() => ({ currentFlowId })),
  editorRef: null,
  setEditorRef: (editorRef: FreeLayoutPluginContext | null) => set(() => ({ editorRef })),
  flowData: {},
  setFlowData: (flowData: FlowData) => set(() => ({ flowData })),
  configData: data,
  setConfigData: (configData: GlobalConfigData) => set(() => ({ configData })),
  initFlowData: () => set(() => ({ flowData: {} }))
}));

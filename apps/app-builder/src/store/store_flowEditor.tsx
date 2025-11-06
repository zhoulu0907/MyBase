import { create } from 'zustand';
export interface FlowEditorState {
  businessId: string;
  setBusinessId: (businessId: string) => void;
  currentFlowId: string;
  setCurrnetFlowId: (versionId: string) => void;
}

export const useFlowEditorStor = create<FlowEditorState>((set) => ({
  businessId: '',
  setBusinessId: (businessId: string) => set(() => ({ businessId })),
  currentFlowId: '',
  setCurrnetFlowId: (currentFlowId: string) => set(() => ({ currentFlowId }))
}));

import { signal } from '@preact/signals-react';

export const createFlowPageEditorSignal = () => {
  const flowId = signal<string>('');
  const setFlowId = (id: string) => {
    flowId.value = id;
  };

  const clearFlowId = () => {
    flowId.value = '';
  };
  return {
    flowId,
    setFlowId,
    clearFlowId
  };
};

export const useFlowPageEditorSignal = createFlowPageEditorSignal();

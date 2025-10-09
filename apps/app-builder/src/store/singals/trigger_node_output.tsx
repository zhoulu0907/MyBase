import { signal } from '@preact/signals-react';

export const createTriggerNodeOutputSignal = () => {
  const nodeOutputs = signal<{
    [key: string]: any;
  }>({});

  const addTriggerNodeOutput = (nodeId: string, output: any) => {
    nodeOutputs.value[nodeId] = output;
  };

  const removeTriggerNodeOutput = (nodeId: string) => {
    delete nodeOutputs.value[nodeId];
  };

  const getTriggerNodeOutput = (nodeId: string) => {
    if (nodeOutputs.value[nodeId]) {
      return nodeOutputs.value[nodeId];
    }

    return {};
  };

  return {
    nodeOutputs,
    addTriggerNodeOutput,
    removeTriggerNodeOutput,
    getTriggerNodeOutput
  };
};

// 创建默认的 store 实例（向后兼容）
export const triggerNodeOutputSignal = createTriggerNodeOutputSignal();

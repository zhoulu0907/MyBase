import { signal } from '@preact/signals-react';

export const createTriggerEditorSignal = () => {
  const nodeData = signal<{ [key: string]: any }>({});
  const setNodeData = (nodeID: string, data: { [key: string]: any }) => {
    nodeData.value = { ...nodeData.value, [nodeID]: data };
  };
  const clearNodeData = () => {
    nodeData.value = {};
  };

  const nodeId = signal<string | undefined>(undefined);
  const setNodeId = (id: string | undefined) => {
    nodeId.value = id;
  };

  return {
    nodeData,
    setNodeData,
    clearNodeData,

    nodeId,
    setNodeId
  };
};

// 创建默认的 store 实例（向后兼容）
export const triggerEditorSignal = createTriggerEditorSignal();

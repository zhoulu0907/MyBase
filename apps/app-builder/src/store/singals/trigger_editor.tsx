import type { FlowNodeJSON } from '@flowgram.ai/fixed-layout-editor';
import { signal } from '@preact/signals-react';

export const createTriggerEditorSignal = () => {
  const nodes = signal<any[]>([]);
  const setNodes = (newNodes: FlowNodeJSON[]) => {
    nodes.value = newNodes;
  };

  const nodeData = signal<{ [key: string]: any }>({});
  const setNodeData = (nodeID: string, data: { [key: string]: any }) => {
    nodeData.value = { ...nodeData.value, [nodeID]: data };
  };
  const setAllNodeData = (data:object) => {
    nodeData.value = data;
  };
  const clearNodeData = () => {
    nodeData.value = {};
  };

  const nodeId = signal<string | undefined>(undefined);
  const setNodeId = (id: string | undefined) => {
    nodeId.value = id;
  };

  const flowId = signal<string>();
  const setFlowId = (id: string) => {
    flowId.value = id;
  };

  const pageId = signal<string>();
  const setPageId = (id: string) => {
    pageId.value = id;
  };

  return {
    nodes,
    setNodes,

    nodeData,
    setNodeData,
    setAllNodeData,
    clearNodeData,

    nodeId,
    setNodeId,

    flowId,
    setFlowId,

    pageId,
    setPageId
  };
};

// 创建默认的 store 实例（向后兼容）
export const triggerEditorSignal = createTriggerEditorSignal();

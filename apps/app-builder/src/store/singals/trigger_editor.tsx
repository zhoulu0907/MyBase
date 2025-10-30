import type { FlowNodeJSON } from '@flowgram.ai/fixed-layout-editor';
import { signal } from '@preact/signals-react';

export const createTriggerEditorSignal = () => {
  // 节点列表
  const nodes = signal<any[]>([]);
  const setNodes = (newNodes: FlowNodeJSON[]) => {
    nodes.value = newNodes;
  };

  // 节点配置
  const nodeData = signal<{ [key: string]: any }>({});
  const setNodeData = (nodeID: string, data: { [key: string]: any }) => {
    nodeData.value = { ...nodeData.value, [nodeID]: data };
  };
  const setAllNodeData = (data: object) => {
    nodeData.value = data;
  };
  const deleteNodeData = (nodeID: string) => {
    const { [nodeID]: _, ...rest } = nodeData.value;
    nodeData.value = rest;
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

  const mainEntities = signal<any[]>([]);
  const setMainEntities = (entities: any[]) => {
    mainEntities.value = entities;
  };

  const subEntities = signal<any[]>([]);
  const setSubEntities = (entities: any[]) => {
    subEntities.value = entities;
  };

  const invalidNodes = signal<Record<string, boolean>>({});
  const setInvalidNode = (nodeId: string, invalid: boolean) => {
    invalidNodes.value = { ...invalidNodes.value, [nodeId]: invalid };
  };
  const isInvalidNode = (nodeId: string) => {
    return invalidNodes.value[nodeId] == true || false;
  };

  return {
    nodes,
    setNodes,

    nodeData,
    setNodeData,
    setAllNodeData,
    deleteNodeData,
    clearNodeData,

    nodeId,
    setNodeId,

    flowId,
    setFlowId,

    invalidNodes,
    setInvalidNode,
    isInvalidNode,

    mainEntities,
    setMainEntities,

    subEntities,
    setSubEntities
  };
};

// 创建默认的 store 实例（向后兼容）
export const triggerEditorSignal = createTriggerEditorSignal();

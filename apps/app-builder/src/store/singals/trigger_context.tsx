import { signal } from '@preact/signals-react';

export const createTriggerContextSignal = () => {
  const nodeContext = signal<{
    [key: string]: Set<string>;
  }>({});

  const addRelatedNode = (nodeId: string, relatedNodeId: string) => {
    const relNodes = nodeContext.value[nodeId];
    if (relNodes) {
      relNodes.add(relatedNodeId);
    }
  };

  const removeRelatedNode = (nodeId: string, relatedNodeId: string) => {
    const relNodes = nodeContext.value[nodeId];
    if (relNodes) {
      relNodes.delete(relatedNodeId);
    }
  };

  const getRelatedNodeIds = (nodeId: string) => {
    const relNodes = nodeContext.value[nodeId];
    if (relNodes) {
      return relNodes;
    }
    return new Set<string>();
  };

  const deleteRelatedNode = (nodeId: string) => {
    if (nodeContext.value[nodeId]) {
      delete nodeContext.value[nodeId];
    }
  };

  return {
    nodeContext,
    addRelatedNode,
    removeRelatedNode,
    getRelatedNodeIds,
    deleteRelatedNode
  };
};

// 创建默认的 store 实例（向后兼容）
export const triggerContextSignal = createTriggerContextSignal();

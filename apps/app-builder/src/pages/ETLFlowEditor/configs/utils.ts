import { previewETLFlowData } from '@onebase/app';
import { etlEditorSignal, ETLNodeType } from '@onebase/common';

/**
 * 根据 graphData 查找指向指定 targetNodeId 的边，并返回其来源节点 ID（sourceNodeID）。
 * @param graphData - 拓扑数据，包含 edges 数组
 * @param targetNodeId - 目标节点 ID
 * @returns 来源节点 ID（sourceNodeID），若未找到则返回空字符串
 */
export function getSourceNodeIdsByTarget(graphData: any, targetNodeId: string): string[] {
  if (graphData && Array.isArray(graphData.edges)) {
    const edges = graphData.edges.filter((e: any) => e.targetNodeID === targetNodeId);
    if (edges) {
      return edges.map((e: any) => e.sourceNodeID);
    }
  }
  return [];
}

/**
 * 递归获取所有指定节点的下游节点（子孙节点）ID 数组
 * @param graphData - 拓扑数据，包含 edges 数组
 * @param currentNodeId - 当前节点 ID
 * @param visited - (内部使用) 已访问过的节点集合，用于防止死循环
 * @returns 所有下游节点 ID 数组（去重）
 */
export function getAllDownstreamNodeIds(
  graphData: any,
  currentNodeId: string,
  visited: Set<string> = new Set()
): string[] {
  if (!graphData || !Array.isArray(graphData.edges) || !currentNodeId) {
    return [];
  }
  if (visited.has(currentNodeId)) {
    // 避免循环引用导致死循环
    return [];
  }
  visited.add(currentNodeId);

  const directTargets = graphData.edges
    .filter((edge: any) => edge.sourceNodeID === currentNodeId)
    .map((edge: any) => edge.targetNodeID);

  // 递归查找所有下游节点
  let allDownstream: Set<string> = new Set();
  for (const targetId of directTargets) {
    allDownstream.add(targetId);
    // 合并递归结果
    const childTargets = getAllDownstreamNodeIds(graphData, targetId, visited);
    for (const id of childTargets) {
      allDownstream.add(id);
    }
  }

  return Array.from(allDownstream);
}

export const clearDownStreamNodeConfig = (curNodeId: string, graphData: any, nodeData: any) => {
  const { setNodeData } = etlEditorSignal;
  console.log(curNodeId);
  console.log('graphData: ', graphData);
  console.log('nodeData: ', nodeData);

  const nodeIds = getAllDownstreamNodeIds(graphData, curNodeId);
  console.log(nodeIds);

  for (const nodeId of nodeIds) {
    const curNodeData = nodeData[nodeId];

    switch (curNodeData.type) {
      case ETLNodeType.INPUT_NODE:
        break;
      case ETLNodeType.OUTPUT_NODE:
        curNodeData.config = {
          ...curNodeData.config,
          fields: []
        };
        curNodeData.output = {
          verified: false
        };
        break;
      case ETLNodeType.JOIN_NODE:
        curNodeData.config = {
          ...curNodeData.config,
          leftNodeId: undefined,
          rightNodeId: undefined,
          joinType: '', // 连接类型
          fieldPairs: [],
          mappings: []
        };
        curNodeData.output = {
          verified: false,
          fields: []
        };
        break;
      case ETLNodeType.UNION_NODE:
        curNodeData.config = {
          ...curNodeData.config,
          data: [],
          colTitles: []
        };
        curNodeData.output = {
          verified: false,
          fields: []
        };
        break;
      case ETLNodeType.SQL_NODE:
        break;
      default:
        break;
    }

    console.log('curNodeData: ', curNodeData);
    setNodeData(nodeId, curNodeData);
  }
};

export const setNodeDataAndResetDownstream = (
  payload: any,
  curNodeId: string,
  graphDataValue: any,
  nodeDataValue: any
) => {
  const { setNodeData } = etlEditorSignal;

  setNodeData(curNodeId, payload);
  clearDownStreamNodeConfig(curNodeId, graphDataValue, nodeDataValue);
};

export interface PreviewData {
  columns: any[];
  data: any[];
}

export const handlePreviewData = async (
  graphData: any,
  nodeData: any,
  curNode: any,
  setPreviewData: (data: any) => void
) => {
  const nodes = graphData.nodes?.map((node: any) => {
    if (node.id === curNode.id) {
      return curNode;
    }
    return {
      id: node.id,
      title: nodeData[node.id].title || '',
      description: nodeData[node.id].description || '',
      type: node.type,
      config: nodeData[node.id].config || {},
      output: nodeData[node.id].output || {},
      meta: node.meta || {}
    };
  });

  const edges = graphData.edges?.map((edge: any) => {
    return {
      sourceNodeId: edge.sourceNodeID,
      targetNodeId: edge.targetNodeID
    };
  });

  const res = await previewETLFlowData({
    nodeId: curNode.id,
    workflow: {
      nodes: nodes,
      edges: edges
    }
  });

  console.log('res: ', res);

  if (res) {
    setPreviewData(res);
  }
};

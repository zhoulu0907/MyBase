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

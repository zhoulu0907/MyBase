import type { FreeLayoutPluginContext } from '@flowgram.ai/free-layout-editor';
import { etlEditorSignal } from '@onebase/common';
import { useEffect, useRef } from 'react';

export interface DeletedEdge {
  sourceNodeID: string;
  targetNodeID: string;
  edgeId: string;
}

export interface UseEdgeDeletionListenerOptions {
  editorRef: React.RefObject<FreeLayoutPluginContext | null>;
  initDataNodesLength: number;
  onEdgeDeleted?: (deletedEdges: DeletedEdge[]) => void;
}

/**
 * 监听边的删除和新增
 * @param options 配置选项
 */
export function useEdgeDeletionListener(options: UseEdgeDeletionListenerOptions) {
  const { editorRef, initDataNodesLength, onEdgeDeleted } = options;
  const { graphData } = etlEditorSignal;

  const previousEdgeIdsRef = useRef<Set<string>>(new Set());
  const previousEdgesMapRef = useRef<Map<string, { sourceNodeID: string; targetNodeID: string }>>(new Map());

  useEffect(() => {
    if (!editorRef.current || !initDataNodesLength) return;

    // 初始化之前的边信息集合（保存完整的边信息，包括 source 和 target）
    const initEdges = editorRef.current.document.linesManager.getAllLines() || [];
    initEdges.forEach((line) => {
      if (line.fromPort?.node?.id && line.toPort?.node?.id) {
        previousEdgesMapRef.current.set(line.id, {
          sourceNodeID: line.fromPort.node.id,
          targetNodeID: line.toPort.node.id
        });
      }
    });
    previousEdgeIdsRef.current = new Set(initEdges.map((line) => line.id));

    const dispose = editorRef.current.document.linesManager.onAvailableLinesChange(() => {
      if (!editorRef.current) return;

      // 获取当前的边列表
      const currentEdges = editorRef.current.document.linesManager.getAllLines() || [];
      const currentEdgeIds = new Set(currentEdges.map((line) => line.id));

      // 获取之前的边ID集合
      const previousEdgeIds = previousEdgeIdsRef.current;

      // 找出被删除的边ID
      const deletedEdgeIds = Array.from(previousEdgeIds).filter((id) => !currentEdgeIds.has(id));

      if (deletedEdgeIds.length > 0) {
        // 从之前保存的边信息中获取被删除边的 source 和 target
        const deletedEdges = getDeletedEdgesInfo(deletedEdgeIds, previousEdgesMapRef.current, graphData.value);

        console.log('边被删除:', deletedEdges);
        if (onEdgeDeleted) {
          onEdgeDeleted(deletedEdges);
        }
      }

      // 找出新增的边
      const addedEdgeIds = Array.from(currentEdgeIds).filter((id) => !previousEdgeIds.has(id));
      if (addedEdgeIds.length > 0) {
        // 更新新增边的信息到 previousEdgesMapRef
        updateAddedEdgesInfo(currentEdges, addedEdgeIds, previousEdgesMapRef.current);
        console.log('边被新增:', addedEdgeIds);
      }

      // 更新之前的边ID集合和边信息映射
      previousEdgeIdsRef.current = currentEdgeIds;
      // 清理已删除的边信息
      deletedEdgeIds.forEach((id) => previousEdgesMapRef.current.delete(id));
    });

    return () => {
      dispose?.dispose();
    };
  }, [initDataNodesLength, onEdgeDeleted]);
}

/**
 * 获取被删除边的详细信息
 */
function getDeletedEdgesInfo(
  deletedEdgeIds: string[],
  previousEdgesMap: Map<string, { sourceNodeID: string; targetNodeID: string }>,
  graphData: any
): DeletedEdge[] {
  return deletedEdgeIds
    .map((edgeId) => {
      // 优先从之前保存的边信息中获取
      const edgeInfo = previousEdgesMap.get(edgeId);
      if (edgeInfo) {
        return {
          sourceNodeID: edgeInfo.sourceNodeID,
          targetNodeID: edgeInfo.targetNodeID,
          edgeId
        };
      }

      // 如果之前没有保存，尝试从 graphData 中获取
      const edgeFromGraphData = graphData.edges?.find((edge: any) => {
        const edgeIdFromGraph = `${edge.sourceNodeID}_-${edge.targetNodeID}_`;
        return edgeIdFromGraph === edgeId;
      });

      if (edgeFromGraphData) {
        return {
          sourceNodeID: edgeFromGraphData.sourceNodeID,
          targetNodeID: edgeFromGraphData.targetNodeID,
          edgeId
        };
      }

      return null;
    })
    .filter(Boolean) as DeletedEdge[];
}

/**
 * 更新新增边的信息
 */
function updateAddedEdgesInfo(
  currentEdges: any[],
  addedEdgeIds: string[],
  previousEdgesMap: Map<string, { sourceNodeID: string; targetNodeID: string }>
) {
  currentEdges.forEach((line) => {
    if (addedEdgeIds.includes(line.id) && line.fromPort?.node?.id && line.toPort?.node?.id) {
      previousEdgesMap.set(line.id, {
        sourceNodeID: line.fromPort.node.id,
        targetNodeID: line.toPort.node.id
      });
    }
  });
}

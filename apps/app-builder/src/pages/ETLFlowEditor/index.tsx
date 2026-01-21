import { Button, Input, Message } from '@arco-design/web-react';
import { IconArrowLeft, IconEdit } from '@arco-design/web-react/icon';
import {
    EditorRenderer,
    FreeLayoutEditorProvider,
    type FreeLayoutPluginContext,
    type WorkflowJSON
} from '@flowgram.ai/free-layout-editor';
import { craeteETLFlow, getETLFlow, updateETLFlow } from '@onebase/app';
import { etlEditorSignal, getHashQueryParam } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import NodeConfigPage from './components/drawer';
import ETLFlowPanel from './components/panel';
import { clearNodeConfig } from './configs/utils';
import { useEdgeDeletionListener, type DeletedEdge } from './hooks/use-edge-deletion-listener';
import { useEditorProps } from './hooks/use-editor-props';
import styles from './index.module.less';
import { FlowNodeRegistries } from './nodes';

const ETLFlowEditorPage: React.FC = () => {
  useSignals();

  const { tenantId } = useParams();

  const navigate = useNavigate();
  const refWrapper = useRef<HTMLDivElement>(null);
  const editorRef = useRef<FreeLayoutPluginContext | null>(null);

  const [flowName, setFlowName] = useState<string>('数据流名称');
  const [isEditFlowName, setIsEditFlowName] = useState<boolean>(false);

  const [initData, setInitData] = useState<WorkflowJSON>({
    nodes: [],
    edges: []
  });
  const editorProps = useEditorProps(FlowNodeRegistries);

  const { graphData, nodeData, setGraphData, setAllNodeData, clearNodeData, clearCurNode } = etlEditorSignal;

  const resetFlowState = useCallback(() => {
    // 清空全局信号缓存，避免上一次编辑残留
    clearCurNode();
    clearNodeData();
    setGraphData({
      nodes: [],
      edges: []
    });
    setInitData({
      nodes: [],
      edges: []
    });
    setFlowName('数据流名称');
  }, [clearCurNode, clearNodeData, setGraphData]);

  const backToDataFactory = () => {
    const appId = getHashQueryParam('appId');

    navigate(`/onebase/${tenantId}/home/create-app/data-factory?appId=${appId}`);
  };

  useEffect(() => {
    const flowId = getHashQueryParam('flowId');
    // 每次进入页面先重置，避免复用同一信号导致脏数据
    resetFlowState();
    if (flowId) {
      handleLoadETLFlow(flowId);
    }
    // 离开时也清理，防止下一次进入复用旧数据

  }, []);

  useEffect(() => {
    console.log('initData: ', initData);
  }, [initData]);

  // 处理边删除后的逻辑
  const handleEdgeDeleted = useCallback((deletedEdges: DeletedEdge[]) => {
    // deletedEdges: 被删除的边的信息数组，包含 sourceNodeID 和 targetNodeID
    console.log('处理边删除:', deletedEdges);

    // 遍历被删除的边，处理每条边的删除
    deletedEdges.forEach((edge) => {
      const { sourceNodeID, targetNodeID } = edge;
      console.log(`边被删除: ${sourceNodeID} -> ${targetNodeID}`);
      clearNodeConfig(targetNodeID, nodeData.value);
    });
  }, []);

  // 监听边的删除和新增
  useEdgeDeletionListener({
    editorRef,
    initDataNodesLength: initData.nodes.length,
    onEdgeDeleted: handleEdgeDeleted
  });

  const handleLoadETLFlow = async (flowId: string) => {
    const res = await getETLFlow(flowId);
    if (res) {
      setFlowName(res.flowName);
    }

    if (res.config && res.config.edges) {
      const graphData = {
        edges: res.config.edges.map((edge: any) => {
          return {
            sourceNodeID: edge.sourceNodeId,
            targetNodeID: edge.targetNodeId
          };
        }),
        nodes: res.config.nodes.map((node: any) => {
          return {
            data: {
              id: node.id,
              title: node.title,
              type: node.type
            },
            id: node.id,
            meta: node.meta,
            type: node.type
          };
        })
      };
      setGraphData(graphData);
      setInitData(graphData);
    }

    if (res.config && res.config.nodes) {
      const nodesRes = res.config.nodes.reduce((acc: any, node: any) => {
        acc[node.id] = {
          config: node.config,
          title: node.title,
          description: node.description,
          output: node.output,
          type: node.type
        };
        return acc;
      }, {});
      setAllNodeData(nodesRes);
    }
  };

  const handleSave = async () => {
    console.log('node data: ', nodeData.value);

    console.log('graph data: ', graphData.value);

    const appId = getHashQueryParam('appId');
    if (!appId) {
      Message.error('应用ID不存在');
      return;
    }

    const nodes = graphData.value.nodes?.map((node: any) => {
      return {
        id: node.id,
        title: nodeData.value[node.id].title || '',
        description: nodeData.value[node.id].description || '',
        type: node.type,
        config: nodeData.value[node.id].config || {},
        output: nodeData.value[node.id].output || {},
        meta: node.meta || {}
      };
    });

    const edges = graphData.value.edges?.map((edge: any) => {
      return {
        sourceNodeId: edge.sourceNodeID,
        targetNodeId: edge.targetNodeID
      };
    });

    let req = {
      applicationId: appId,
      flowName: flowName,
      config: {
        nodes: nodes,
        edges: edges
      }
    };

    const flowId = getHashQueryParam('flowId');
    if (flowId) {
      const res = await updateETLFlow({ id: flowId, ...req });

      Message.success('更新成功');
    } else {
      const res = await craeteETLFlow(req);

      Message.success('创建成功');
      const appId = getHashQueryParam('appId');
      navigate(`/onebase/${tenantId}/etl_editor?flowId=${res}&appId=${appId}`);
    }
  };

  return (
    <div className={styles.etlFlowEditorPage}>
      <div className={styles.etlFlowEditorHeader}>
        <div className={styles.etlFlowEditorHeaderLeft}>
          <IconArrowLeft onClick={backToDataFactory} />

          {isEditFlowName ? (
            <Input
              value={flowName}
              onChange={(value: string) => setFlowName(value)}
              onBlur={() => setIsEditFlowName(false)}
              onPressEnter={() => setIsEditFlowName(false)}
              onKeyDown={(e: React.KeyboardEvent<HTMLInputElement>) => {
                if (e.key === 'Escape') {
                  setIsEditFlowName(false);
                }
              }}
            />
          ) : (
            <div className={styles.flowName}>{flowName}</div>
          )}

          <IconEdit onClick={() => setIsEditFlowName(true)} />
        </div>
        <div className={styles.etlFlowEditorHeaderRight}>
          <Button type="primary" onClick={handleSave}>
            保存
          </Button>
        </div>
      </div>
      <div className={styles.etlFlowEditorContent}>
        {initData && (
          <FreeLayoutEditorProvider
            ref={editorRef}
            key={initData?.nodes?.length ?? 0}
            initialData={initData}
            {...editorProps}
          >
            <div className={styles.sidebar}>
              <ETLFlowPanel />
            </div>
            <div
              className={styles.main}
              ref={refWrapper}
              onClick={(e: React.MouseEvent<HTMLDivElement>) => {
                if (
                  (e.target as HTMLElement).classList &&
                  (e.target as HTMLElement).classList.contains('gedit-flow-background-layer')
                ) {
                  etlEditorSignal.clearCurNode();
                }
              }}
            >
              <EditorRenderer />
              <NodeConfigPage refWrapper={refWrapper} />
            </div>
          </FreeLayoutEditorProvider>
        )}
      </div>
    </div>
  );
};

export { ETLFlowEditorPage };

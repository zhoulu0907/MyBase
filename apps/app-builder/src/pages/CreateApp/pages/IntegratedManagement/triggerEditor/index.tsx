import { EditorRenderer, FixedLayoutEditorProvider, type FlowDocumentJSON } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import '@flowgram.ai/fixed-layout-editor/index.css';
import { ENTITY_TYPE, getAppEntities, getFlowMgmt, TriggerType } from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { useEffect, useRef, useState } from 'react';
import { SidebarProvider, SidebarRenderer } from './components/sidebar';
import { Tools } from './components/tools';
import { useEditorProps } from './hooks/use-editor-props';
import styles from './index.module.less';
import {
  StartApiInitData,
  StartDateFieldInitData,
  StartEntityInitData,
  StartFormInitData,
  StartTimeInitData
} from './initial-data';
import { FlowNodeRegistries } from './nodes';

const TriggerEditor = () => {
  const editorProps = useEditorProps(FlowNodeRegistries);

  const { setNodeId, nodeId, setFlowId, setNodeData, setAllNodeData, setMainEntities, setSubEntities } =
    triggerEditorSignal;

  const [initData, setInitData] = useState<FlowDocumentJSON>();
  const sidebarContainerRef = useRef<HTMLDivElement>(null);

  useSignals();

  useEffect(() => {
    const flowId = getHashQueryParam('flowId');
    if (flowId) {
      setFlowId(flowId);
      initFlowData(flowId);
    }

    // 获取应用对应需要用到的实体
    const appId = getHashQueryParam('appId');
    if (appId) {
      handleGetAppEntities(appId);
    }
  }, [window.location.hash]);

  const handleGetAppEntities = async (appId: string) => {
    const res = await getAppEntities(appId);

    // TODO(mickey): 需要优化,
    if (res && res.entities) {
      console.log('res.entities: ', res.entities);
      setMainEntities(
        res.entities.filter(
          (entity: any) => entity.entityType === ENTITY_TYPE.MAIN || entity.entityType === ENTITY_TYPE.INDEP
        )
      );
      setSubEntities(res.entities.filter((entity: any) => entity.entityType === ENTITY_TYPE.SUB));
    }
  };

  // 载入初始化数据
  const initFlowData = async (id: string) => {
    const res = await getFlowMgmt(id);

    // 已保存的节点数据回显
    if (res.processDefinition?.length) {
      const processDefinitionJson = JSON.parse(res.processDefinition);
      let data = {};
      let nodes = processDefinitionJson.nodes || [];

      for (let item of nodes) {
        data = { ...data, [item.id]: { ...item.data, output: item.output } };
        if (item.blocks) {
          // 递归初始化blocks数据
          data = initBlocksData(item.blocks, data);
        }
      }

      console.log('nodeData', data);

      // 初始化节点的输出
      Object.values(data).forEach((item: any) => {
        if (item.id && item.output) {
          triggerNodeOutputSignal.addTriggerNodeOutput(item.id, item.output);
        }
      });

      console.log('nodeOutputs: ', triggerNodeOutputSignal.nodeOutputs.value);

      setAllNodeData(data);
      setInitData({ nodes: nodes });
    } else {
      // 对开始节点数据初始化
      switch (res.triggerType) {
        case TriggerType.FORM: {
          setInitData(StartFormInitData);
          const formInitialData = {
            ...StartFormInitData.nodes[0].data.initialData,
            filterCondition: [],
            pageUuid: res.triggerConfig.pageUuid,
            triggerRange: res.triggerConfig.triggerRange
          };
          setNodeData(StartFormInitData.nodes[0].id, formInitialData);
          setNodeData(StartFormInitData.nodes[1].id, StartFormInitData.nodes[1].data.initialData);
          break;
        }

        case TriggerType.ENTITY: {
          setInitData(StartEntityInitData);
          const entityInitialData = {
            ...StartEntityInitData.nodes[0].data.initialData,
            tableName: res.triggerConfig.tableName
          };
          setNodeData(StartEntityInitData.nodes[0].id, entityInitialData);
          setNodeData(StartEntityInitData.nodes[1].id, StartEntityInitData.nodes[1].data.initialData);
          break;
        }

        case TriggerType.TIME: {
          setInitData(StartTimeInitData);
          setNodeData(StartTimeInitData.nodes[0].id, StartTimeInitData.nodes[0].data.initialData);
          setNodeData(StartTimeInitData.nodes[1].id, StartTimeInitData.nodes[1].data.initialData);
          break;
        }

        case TriggerType.DATE_FIELD: {
          setInitData(StartDateFieldInitData);
          const dateFieldInitialData = {
            ...StartDateFieldInitData.nodes[0].data.initialData,
            tableName: res.triggerConfig.tableName
          };
          setNodeData(StartDateFieldInitData.nodes[0].id, dateFieldInitialData);
          setNodeData(StartDateFieldInitData.nodes[1].id, StartDateFieldInitData.nodes[1].data.initialData);
          break;
        }

        case TriggerType.API: {
          setInitData(StartApiInitData);
          break;
        }
      }
    }
  };

  const initBlocksData = (blocks: any[], data: any) => {
    for (let item of blocks) {
      data = { ...data, [item.id]: { ...item.data, output: item.output } };
      if (item.blocks) {
        data = initBlocksData(item.blocks, data);
      }
    }
    return data;
  };

  useEffect(() => {
    console.log('initData: ', initData);
    if (initData) {
      triggerEditorSignal.setNodes(initData.nodes);
    }
  }, [initData]);

  return (
    <div className={styles.triggerEditor}>
      {initData && (
        <FixedLayoutEditorProvider initialData={initData} {...editorProps}>
          <SidebarProvider>
            <div
              className={styles.container}
              onClick={(e) => {
                let target = e.target as HTMLElement | null;
                if (target) {
                  if (
                    target.classList &&
                    (target.classList.contains('gedit-playground-layer') ||
                      target.classList.contains('flow-canvas-adder'))
                  ) {
                    setNodeId(undefined);
                  }
                }
              }}
            >
              <div className={styles.layout}>
                <EditorRenderer className={styles.editor} />
                <Tools />
              </div>
              <div
                className={styles.sidebarContainer}
                ref={sidebarContainerRef}
                style={{
                  width: nodeId.value ? '800px' : '0px'
                }}
              >
                <SidebarRenderer refWrapper={sidebarContainerRef} />
              </div>
            </div>
          </SidebarProvider>
        </FixedLayoutEditorProvider>
      )}
    </div>
  );
};

export default TriggerEditor;

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
  StartBpmInitData,
  StartDateFieldInitData,
  StartEntityInitData,
  StartFormInitData,
  StartTimeInitData
} from './initial-data';
import { FlowNodeRegistries } from './nodes';

const TriggerEditor = () => {
  const editorProps = useEditorProps(FlowNodeRegistries);

  const {
    setNodeId,
    nodeId,
    setFlowId,
    flowId,
    // setPageId,
    setNodeData,
    setAllNodeData,
    setMainEntities,
    setSubEntities
  } = triggerEditorSignal;

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
    console.log('res: ', res);
    if (res && res.entities) {
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
    console.log('res: ', res);

    // if (res.triggerConfig && res.triggerConfig.pageId) {
    //   setPageId(res.triggerConfig.pageId);
    // }

    // 已保存的节点数据回显
    if (res.processDefinition?.length) {
      console.log('res.processDefinition: ', res.processDefinition);
      const processDefinitionJson = JSON.parse(res.processDefinition);
      let data = {};
      let nodes = processDefinitionJson.nodes || [];

      console.log('nodes: ', nodes);

      for (let item of nodes) {
        data = { ...data, [item.id]: item.data };
        // 初始化输出节点
        // console.log('item.id: ', item.id);
        if (item.output) {
          //   console.log('item.output: ', item.output);
          triggerNodeOutputSignal.addTriggerNodeOutput(item.id, item.output);
        }
      }

      console.log('nodeData', data);
      setAllNodeData(data);
      setInitData({ nodes: nodes });
    } else {
      switch (res.triggerType) {
        case TriggerType.FORM:
          setInitData(StartFormInitData);
          const formInitialData = {
            ...StartFormInitData.nodes[0].data.initialData,
            filterCondition: [],
            pageId: res.triggerConfig.pageId,
            triggerRange: res.triggerConfig.triggerRange
          };
          setNodeData(StartFormInitData.nodes[0].id, formInitialData);
          setNodeData(StartFormInitData.nodes[1].id, StartFormInitData.nodes[1].data.initialData);
          break;
        case TriggerType.ENTITY:
          setInitData(StartEntityInitData);
          const entityInitialData = {
            ...StartEntityInitData.nodes[0].data.initialData,
            entityId: res.triggerConfig.entityId
          };
          setNodeData(StartEntityInitData.nodes[0].id, entityInitialData);
          setNodeData(StartEntityInitData.nodes[1].id, StartEntityInitData.nodes[1].data.initialData);
          break;
        case TriggerType.TIME:
          setInitData(StartTimeInitData);
          setNodeData(StartTimeInitData.nodes[0].id, StartTimeInitData.nodes[0].data.initialData);
          setNodeData(StartTimeInitData.nodes[1].id, StartTimeInitData.nodes[1].data.initialData);
          break;
        case TriggerType.DATE_FIELD:
          setInitData(StartDateFieldInitData);
          const dateFieldInitialData = {
            ...StartDateFieldInitData.nodes[0].data.initialData,
            entityId: res.triggerConfig.entityId
          };
          setNodeData(StartDateFieldInitData.nodes[0].id, dateFieldInitialData);
          setNodeData(StartDateFieldInitData.nodes[1].id, StartDateFieldInitData.nodes[1].data.initialData);
          break;
        case TriggerType.API:
          setInitData(StartApiInitData);
          break;
        case TriggerType.BPM:
          setInitData(StartBpmInitData);
          break;
      }
    }
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

import { EditorRenderer, FixedLayoutEditorProvider, type FlowDocumentJSON } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import '@flowgram.ai/fixed-layout-editor/index.css';
import { getFlowMgmt, TriggerType } from '@onebase/app';
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
  const { setNodeId, nodeId, setFlowId, flowId, setPageId, setNodeData, setAllNodeData } = triggerEditorSignal;
  const [initData, setInitData] = useState<FlowDocumentJSON>();
  const sidebarContainerRef = useRef<HTMLDivElement>(null);

  useSignals();

  useEffect(() => {
    const flowId = getHashQueryParam('flowId');
    if (flowId) {
      setFlowId(flowId);
    }
  }, [window.location.hash]);

  useEffect(() => {
    if (flowId.value) {
      console.log('flowId: ', flowId.value);
      initFlowData(flowId.value);
    }
  }, [flowId]);

  // 载入初始化数据
  const initFlowData = async (id: string) => {
    const res = await getFlowMgmt(id);
    console.log('res: ', res);
    console.log(res.triggerType);

    if (res.triggerConfig && res.triggerConfig.pageId) {
      setPageId(res.triggerConfig.pageId);
    }

    // 已保存的节点数据回显
    if (res.processDefinition?.length) {
      console.log('res.processDefinition: ', res.processDefinition);
      const processDefinitionJson = JSON.parse(res.processDefinition);
      let data = {};
      for (let item of processDefinitionJson) {
        data = { ...data, [item.id]: item.data };
      }
      console.log('nodeData', data);
      setAllNodeData(data);
      setInitData({ nodes: processDefinitionJson });
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
                  width: nodeId.value ? '600px' : '0px'
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

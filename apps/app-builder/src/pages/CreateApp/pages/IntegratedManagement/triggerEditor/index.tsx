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
  const { setNodeId, nodeId, setFlowId, flowId, setPageId, setNodeData } = triggerEditorSignal;
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

    switch (res.triggerType) {
      case TriggerType.FORM:
        setInitData(StartFormInitData);
        setNodeData(StartFormInitData.nodes[0].id, StartFormInitData.nodes[0].data.initialData);
        setNodeData(StartFormInitData.nodes[1].id, StartFormInitData.nodes[1].data.initialData);
        break;
      case TriggerType.ENTITY:
        setInitData(StartEntityInitData);
        break;
      case TriggerType.TIME:
        setInitData(StartTimeInitData);
        break;
      case TriggerType.DATE_FIELD:
        setInitData(StartDateFieldInitData);
        break;
      case TriggerType.API:
        setInitData(StartApiInitData);
        break;
      case TriggerType.BPM:
        setInitData(StartBpmInitData);
        break;
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

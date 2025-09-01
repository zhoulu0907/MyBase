import { EditorRenderer, FixedLayoutEditorProvider, type FlowDocumentJSON } from '@flowgram.ai/fixed-layout-editor';

import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import '@flowgram.ai/fixed-layout-editor/index.css';
import { getFlowMgmt, TriggerType } from '@onebase/app';
import { getHashQueryParam } from '@onebase/common';
import { useEffect, useState } from 'react';
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
  //   const editorProps = useEditorProps(initialData, FlowNodeRegistries);
  const editorProps = useEditorProps(FlowNodeRegistries);
  const { setNodeId, setFlowId, flowId } = triggerEditorSignal;
  const [initData, setInitData] = useState<FlowDocumentJSON>();

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

  const initFlowData = async (id: string) => {
    const res = await getFlowMgmt(id);
    console.log('res: ', res);
    console.log(res.triggerType);

    switch (res.triggerType) {
      case TriggerType.FORM:
        setInitData(StartFormInitData);
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
              <SidebarRenderer />
            </div>
          </SidebarProvider>
        </FixedLayoutEditorProvider>
      )}
    </div>
  );
};

export default TriggerEditor;

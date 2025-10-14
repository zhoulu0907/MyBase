import {
  EditorRenderer,
  FreeLayoutEditorProvider,
  type FlowDocumentJSON 
} from '@flowgram.ai/free-layout-editor';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import '@flowgram.ai/free-layout-editor/index.css';
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
  StartTimeInitData,
  initialData
} from './initial-data';
import { FlowNodeRegistries } from './nodes';

const FlowEditor = () => {
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

  const [initData, setInitData] = useState();
  const sidebarContainerRef = useRef<HTMLDivElement>(null);

  useSignals();

  useEffect(() => {
    initFlowData()
   
  }, [window.location.hash]);


  // 载入初始化数据
  const initFlowData = async () => {
     setInitData(initialData);

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
        <FreeLayoutEditorProvider initialData={initData} {...editorProps}>
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
        </FreeLayoutEditorProvider>
       )} 
    </div>
  );
};

export default FlowEditor;

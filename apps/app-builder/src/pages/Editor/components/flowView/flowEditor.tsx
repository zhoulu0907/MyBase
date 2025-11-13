import React, { useRef, useEffect } from 'react';
import {
  EditorRenderer,
  FreeLayoutEditorProvider,
  type FreeLayoutPluginContext
} from '@flowgram.ai/free-layout-editor';
// import { ZoomSelect } from '../../freeLayout/components/tools/zoom-select';
// import { AutoLayout } from './auto-layout';
import { type FlowDocumentJSON } from '../../freeLayout/typings';
import { useEditorProps } from '../../freeLayout/hooks';
import { nodeRegistries } from '../../freeLayout/nodes';
import { IndexType } from './indexType';
import styles from './index.module.less';
import { initialData } from './initial-data';

interface FlowEditorProps {
  preViewData: FlowDocumentJSON;
}

const FlowEditor: React.FC<FlowEditorProps> = ({ preViewData }) => {
  const ref = useRef<FreeLayoutPluginContext | null>(null);
  // const editorPropsInit = useEditorProps(preViewData, nodeRegistries);
  const editorPropsInit = useEditorProps(initialData, nodeRegistries);
  const editorProps = Object.assign({}, editorPropsInit, { background: true, readonly: false });
  // const getPassConnections = (edges: Array<any>) => {
  //   return edges
  //     .filter((edge) => edge.runStatus === IndexType.COMPLETED)
  //     .map((edge) => `${edge.sourceNodeID}_-${edge.targetNodeID}_`);
  // };

  // useEffect(() => {
  //   if (!ref.current) return;
  //   const passLines = getPassConnections(preViewData?.edges);
  //   const lines = ref?.current?.document.linesManager.getAllLines();
  //   lines?.forEach((line) => {
  //     if (passLines.includes(line.id)) {
  //       line.lockedColor = IndexType.COMPLETED_COLOR;
  //     } else {
  //       line.lockedColor = IndexType.PENDING_COLOR;
  //     }
  //   });
  // }, []);
  return (
    <FreeLayoutEditorProvider {...editorProps} ref={ref}>
      <EditorRenderer className={styles.demoEditor} />
      <div className={styles.zoomSelectWrapper}>
        {/* <ZoomSelect /> */}
        {/* <AutoLayout /> */}
      </div>
    </FreeLayoutEditorProvider>
  );
};

export default FlowEditor;

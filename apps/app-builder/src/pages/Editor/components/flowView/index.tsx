import React, { useState, useRef, useCallback, useEffect } from 'react';
import { Modal } from '@arco-design/web-react';
import {
  EditorRenderer,
  FreeLayoutEditorProvider,
  type FreeLayoutPluginContext
} from '@flowgram.ai/free-layout-editor';
import { nodeRegistries } from '../../freeLayout/nodes';
import { useEditorProps } from '../../freeLayout/hooks';
import { ZoomSelect } from '../../freeLayout/components/tools/zoom-select';
import { initialData } from './initial-data';
import { AutoLayout } from './auto-layout';
import { IndexType } from './indexType';
import styles from './index.module.less';

interface PreviewModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
}

const FlowView: React.FC<PreviewModalProps> = ({ visible, setVisible }) => {
  const ref = useRef<FreeLayoutPluginContext | null>(null);
  const editorPropsInit = useEditorProps(initialData, nodeRegistries);
  const editorProps = Object.assign({}, editorPropsInit, { background: false, readonly: false });
  const [isModalReady, setIsModalReady] = useState(false);

  const afterOpen = async () => {
    setIsModalReady(true);
  };
  const getPassConnections = (edges: Array<any>) => {
    return edges
      .filter((edge) => edge.runStatus === IndexType.PASS)
      .map((edge) => `${edge.sourceNodeID}_-${edge.targetNodeID}_`);
  };

  useEffect(() => {
    if (!ref.current) return;
    const passLines = getPassConnections(initialData?.edges);
    const lines = ref?.current?.document.linesManager.getAllLines();
    lines?.forEach((line) => {
      if (passLines.includes(line.id)) {
        line.lockedColor = IndexType.COMPLETED_COLOR;
      } else {
        line.lockedColor = IndexType.PENDING;
      }
    });
  }, [isModalReady]);
  return (
    <Modal
      className={styles.flowViewModal}
      visible={visible}
      footer={null}
      onCancel={() => setVisible(false)}
      afterOpen={afterOpen}
      afterClose={() => setIsModalReady(false)}
    >
      <div className={styles.header}>
        <div className={styles.left}>
          <div className={styles.title}>流程预览</div>
          <div className={styles.legendItem}>
            <span className={`${styles.legendDot} ${styles.processing}`}></span>处理中
          </div>
          <div className={styles.legendItem}>
            <span className={`${styles.legendDot} ${styles.completed}`}></span>已流转
          </div>
          <div className={styles.legendItem}>
            <span className={`${styles.legendDot} ${styles.pending}`}></span>未流转
          </div>
        </div>
        <div className={styles.right}>下载为图片</div>
      </div>
      <div className={styles.flowViewContent}>
        {isModalReady && (
          <FreeLayoutEditorProvider {...editorProps} ref={ref}>
            <EditorRenderer className={styles.demoEditor} />
            <div className={styles.zoomSelectWrapper}>
              <ZoomSelect />
              <AutoLayout />
            </div>
          </FreeLayoutEditorProvider>
        )}
      </div>
    </Modal>
  );
};
export default FlowView;

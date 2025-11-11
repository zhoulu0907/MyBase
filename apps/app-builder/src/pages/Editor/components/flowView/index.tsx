import React, { useState, useRef, useCallback, useEffect } from 'react';
import { Modal } from '@arco-design/web-react';
import {
  EditorRenderer,
  FreeLayoutEditorProvider,
  type FreeLayoutPluginContext
} from '@flowgram.ai/free-layout-editor';
import { nodeRegistries } from '../../freeLayout/nodes';
import { useEditorProps } from '../../freeLayout/hooks';
import { initialData } from './initial-data';
import styles from './index.module.less';

interface PreviewModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
}

const FlowView: React.FC<PreviewModalProps> = ({ visible, setVisible }) => {
  const ref = useRef<FreeLayoutPluginContext | null>(null);
  const editorPropsInit = useEditorProps(initialData, nodeRegistries);
  const editorProps = Object.assign({}, editorPropsInit, { background: false, readonly: true });
  const [isModalReady, setIsModalReady] = useState(false);

  const afterOpen = async () => {
    setIsModalReady(true);
  };
  const getPassConnections = (edges: Array<any>) => {
    return edges.filter((edge) => edge.status === 'pass').map((edge) => `${edge.sourceNodeID}_-${edge.targetNodeID}_`);
  };

  useEffect(() => {
    if (!ref.current) return;
    const passLines = getPassConnections(initialData?.edges);
    const lines = ref?.current?.document.linesManager.getAllLines();
    lines?.forEach((line) => {
      if (passLines.includes(line.id)) {
        line.lockedColor = '#4FAE7B';
      } else {
        line.lockedColor = '#d9d9d9';
      }
    });
    console.log(lines, passLines);
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
          </FreeLayoutEditorProvider>
        )}
      </div>
    </Modal>
  );
};
export default FlowView;

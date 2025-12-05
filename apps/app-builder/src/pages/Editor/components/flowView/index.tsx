import React, { useState } from 'react';
import { Modal } from '@arco-design/web-react';
import FlowEditor from './flowEditor';
import { getFlowPreview } from '@onebase/app/src/services';
import type { WorkflowJSON } from './indexType';
import styles from './index.module.less';
import '@flowgram.ai/free-layout-editor/index.css';
const sourceNodeIDMap = new Map();
interface PreviewModalProps {
  visible: boolean;
  setVisible: (visible: boolean) => void;
  instanceId?: string;
  businessUuid?: string;
}

const FlowView: React.FC<PreviewModalProps> = ({ visible, setVisible, instanceId = '', businessUuid = '' }) => {
  const [isModalReady, setIsModalReady] = useState(false);
  const [preViewData, setPreviewData] = useState<any>({});
  const afterOpen = () => {
    getFlowPreviewData();
  };

  const normalizeNodes = (obj: WorkflowJSON | undefined) => {
    obj?.edges.forEach((item) => {
      if (item?.type) {
        sourceNodeIDMap.set(item.sourceNodeID + item.targetNodeID, item.type);
      } else {
        item.type = sourceNodeIDMap.get(item.sourceNodeID + item.targetNodeID) || 'PASS';
      }
    });
    const newNodes = obj?.nodes.map((node) => {
      if ('name' in node) {
        return { ...node, data: { ...(node.data || {}), name: node.name } };
      } else if (node.data && 'name' in node.data) {
        return { ...node, name: node.data.name };
      }
      return node;
    });
    return { ...obj, nodes: newNodes };
  };

  const getFlowPreviewData = async () => {
    const res = await getFlowPreview({ instanceId, businessUuid });
    try {
      const parseData = normalizeNodes(JSON.parse(res.bpmDefJson));
      setPreviewData(parseData);
      setIsModalReady(true);
    } catch (error) {
      console.log('预览数据错误');
    }
  };

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
      <div className={styles.flowViewContent}>{isModalReady && <FlowEditor preViewData={preViewData} />}</div>
    </Modal>
  );
};
export default FlowView;

import { Button } from '@arco-design/web-react';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import styles from './index.module.less';

export function FormFooter({ nodeInfo }: { nodeInfo: any }) {
  const { nodeId, nodeData, setNodeId, setNodeData } = triggerEditorSignal;

  const cancel = () => {
    // 取消 关闭弹窗
    setNodeId(undefined);
  };
  const saveNode = () => {
    if (nodeId.value) {
      const formInfo = nodeInfo?.props?.form?.getFieldsValue();
      const param = { ...nodeData.value[nodeId.value], ...formInfo };
      setNodeData(nodeId.value, param);
    }
  };

  return (
    <div className={styles.formFooter}>
      <Button type="outline" style={{ marginRight: '20px' }} onClick={cancel}>
        取消
      </Button>
      <Button type="primary" onClick={saveNode}>
        保存
      </Button>
    </div>
  );
}

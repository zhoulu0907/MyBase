import { Button } from '@arco-design/web-react';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { validateNodeForm } from '../../nodes/utils';
import { getNodeForm, useClientContext } from '@flowgram.ai/free-layout-editor';
import styles from './index.module.less';

export function FormFooter({ nodeInfo }: { nodeInfo: any }) {
  const { nodeId, nodeData, setNodeId, setNodeData } = triggerEditorSignal;
  // 流程数据文档 (固定布局), 存储流程的所有节点数据
  const ctx = useClientContext();

  const cancel = () => {
    // 取消 关闭弹窗
    setNodeId(undefined);
  };
  const saveNode = async () => {
    if (nodeId.value && nodeInfo?.props?.form) {
      // 通过指定 id 获取节点
      const node = ctx.document.getNode(nodeId.value);
      if (node) {
        // 节点表单
        const form = getNodeForm(node);
        validateNodeForm(form, nodeInfo.props.form, false);
      }
      // 获取表单数据
      const formInfo = nodeInfo.props.form.getFieldsValue();
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

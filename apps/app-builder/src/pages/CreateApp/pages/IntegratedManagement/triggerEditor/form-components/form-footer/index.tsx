import { Button } from '@arco-design/web-react';
import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { validateNodeForm } from '../../nodes/utils';
import { getNodeForm, useClientContext } from '@flowgram.ai/fixed-layout-editor';
import styles from './index.module.less';
import { NodeType } from '@onebase/common';

export function FormFooter({ nodeInfo }: { nodeInfo: any }) {
  const { nodeId, nodeData, nodes, setNodeId, setNodeData } = triggerEditorSignal;
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
      console.log('formInfo', formInfo);
      let param = { ...nodeData.value[nodeId.value], ...formInfo };
      const curNode = nodes.value.find((ele) => ele.id === nodeId.value);
      if (curNode.type === NodeType.MODAL) {
        const fields = nodeInfo.props.form.getFieldValue('fields');
        param = { ...param, fields };
      }
      // 过滤掉数据为空的数组  一维数组
      const keys = Object.keys(param);
      for (let key of keys) {
        // 数组
        if (Array.isArray(param[key])) {
          // 过滤掉数据全为空的数据
          param[key] = param[key].filter((ele) => {
            if (Object.prototype.toString.call(ele).slice(8, -1) === 'Object') {
              const itemKeys = Object.keys(ele);
              for (let itemKey of itemKeys) {
                if (ele[itemKey]) {
                  return true;
                }
              }
              return false;
            }
            return true;
          });
        }
      }
      setNodeData(nodeId.value, param);
    }
    setNodeId(undefined);
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

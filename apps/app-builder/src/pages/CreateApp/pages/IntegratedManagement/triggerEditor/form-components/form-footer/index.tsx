import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Button } from '@arco-design/web-react';
import { getNodeForm, useClientContext } from '@flowgram.ai/fixed-layout-editor';
import { NodeType } from '@onebase/common';
import { clearDataOriginNodeId, searchNodeById, validateNodeForm } from '../../nodes/utils';
import styles from './index.module.less';

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
      const originalNodeData = nodeData.value[nodeId.value];
      const formInfo = nodeInfo.props.form.getFieldsValue();
      console.log('original nodeData: ', originalNodeData);
      console.log('formInfo', formInfo);

      let param = { ...nodeData.value[nodeId.value], ...formInfo };
      const curNode = searchNodeById(nodeId.value, nodes.value);

      //   针对有变更的节点，需要清空下游节点的依赖
      switch (curNode.type) {
        case NodeType.DATA_QUERY_MULTIPLE:
        case NodeType.DATA_QUERY: {
          if (
            originalNodeData.dataType != formInfo.dataType ||
            originalNodeData.mainEntityId != formInfo.mainEntityId ||
            originalNodeData.subEntityId != formInfo.subEntityId ||
            originalNodeData.dataNodeId != formInfo.dataNodeId
          ) {
            clearDataOriginNodeId(nodeId.value);
          }

          break;
        }

        case NodeType.DATA_ADD: {
          if (
            originalNodeData.addType != formInfo.addType ||
            originalNodeData.mainEntityId != formInfo.mainEntityId ||
            originalNodeData.subEntityId != formInfo.subEntityId ||
            originalNodeData.dataNodeId != formInfo.dataNodeId
          ) {
            clearDataOriginNodeId(nodeId.value);
          }
          break;
        }

        case NodeType.DATA_DELETE: {
          if (
            originalNodeData.dataType != formInfo.dataType ||
            originalNodeData.mainEntityId != formInfo.mainEntityId ||
            originalNodeData.subEntityId != formInfo.subEntityId
          ) {
            clearDataOriginNodeId(nodeId.value);
          }
          break;
        }

        // TODO(chenyongqiang): 补充其他节点类型

        default:
          break;
      }

      if (curNode && curNode.type === NodeType.MODAL) {
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

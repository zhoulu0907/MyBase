import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Button, Message } from '@arco-design/web-react';
import { ProcessStatus, updateFlowMgmtDefinition } from '@onebase/app';
import React from 'react';
import TriggerEditor from '../../triggerEditor';
import { NodeType } from '../../triggerEditor/nodes/const';
import styles from './index.module.less';

/**
 * 流程编辑页面
 * 集成触发器编辑器作为主内容
 */
const FlowEditorPage: React.FC = () => {
  const { nodeData, nodes, flowId } = triggerEditorSignal;

  const handleSave = async () => {
    console.log('nodes: ', nodes.value);
    console.log('nodeData: ', nodeData.value);

    const processDefinitionJson = nodes.value.map((item) => {
      console.log('item: ', item);
      const { outputs: nodeOutputs, initialData: nodeInitialData, ...restNodeData } = nodeData.value[item.id] || {};

      if (item.type === NodeType.IF) {
        return item;
      }

      const data = {
        id: item.id,
        type: item.type,
        data: {
          ...restNodeData,
          // 覆写的属性写在后面
          title: item.data.title
        }
      };

      return data;
    });

    console.log('processDefinition', processDefinitionJson);
    const params = {
      id: flowId.value || '',
      processDefinition: JSON.stringify({ nodes: processDefinitionJson }),
      processStatus: ProcessStatus.DISABLED
    };
    console.log('params', params);

    const res = await updateFlowMgmtDefinition(params);
    if (res) {
      Message.success('保存成功');
    }
  };

  return (
    <div className={styles.flowEditorPage}>
      <div className={styles.header}>
        <Button type="primary" onClick={handleSave}>
          保存
        </Button>
      </div>
      <div className={styles.body}>
        <TriggerEditor />
      </div>
    </div>
  );
};

export default FlowEditorPage;

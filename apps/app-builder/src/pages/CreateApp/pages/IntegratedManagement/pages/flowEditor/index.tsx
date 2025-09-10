import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Button, Message } from '@arco-design/web-react';
import React from 'react';
import TriggerEditor from '../../triggerEditor';
import styles from './index.module.less';
import { ProcessStatus, updateFlowMgmtDefinition } from '@onebase/app';

/**
 * 流程编辑页面
 * 集成触发器编辑器作为主内容
 */
const FlowEditorPage: React.FC = () => {
  const { nodeData, nodes, flowId } = triggerEditorSignal;

  const handleSave = async () => {
    const processDefinitionJson = nodes.value.map((item) => {
      const data = {...item.data, ...nodeData.value[item.id]}
      return { ...item, data };
    });
    console.log('processDefinition', processDefinitionJson);
    const params = {
      id: flowId || '',
      processDefinition: JSON.stringify(processDefinitionJson),
      processStatus: ProcessStatus.DISABLED
    };
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

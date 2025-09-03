import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { Button } from '@arco-design/web-react';
import React from 'react';
import TriggerEditor from '../../triggerEditor';
import styles from './index.module.less';

/**
 * 流程编辑页面
 * 集成触发器编辑器作为主内容
 */
const FlowEditorPage: React.FC = () => {
  const { nodeData, nodes } = triggerEditorSignal;

  const handleSave = () => {
    console.log('handleSave: ', nodeData.value);
    console.log('nodes: ', nodes.value);
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

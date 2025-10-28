import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import { Button } from '@arco-design/web-react';
import { WorkflowDragService, useService } from '@flowgram.ai/free-layout-editor';
import { LLMNodeRegistry } from './../../freeLayout/nodes/llm/index';

// import { ProcessStatus, updateFlowMgmtDefinition } from '@onebase/app';
import React from 'react';
import { FlowEditor } from '../../freeLayout/index';
import styles from './index.module.less';

/**
 * 流程编辑页面
 * 集成触发器编辑器作为主内容
 */
const FlowEditorPage: React.FC = () => {
  return (
    <div className={styles.flowEditorPage}>
      <div className={styles.body}>
        <FlowEditor />
      </div>
    </div>
  );
};

export default FlowEditorPage;

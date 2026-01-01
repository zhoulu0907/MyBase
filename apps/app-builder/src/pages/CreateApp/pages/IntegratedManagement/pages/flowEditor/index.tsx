import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { triggerNodeOutputSignal } from '@/store/singals/trigger_node_output';
import { Button, Message } from '@arco-design/web-react';
import { ProcessStatus, updateFlowMgmtDefinition } from '@onebase/app';
import React from 'react';
import TriggerEditor from '../../triggerEditor';
import styles from './index.module.less';

/**
 * 流程编辑页面
 * 集成触发器编辑器作为主内容
 */
const FlowEditorPage: React.FC = () => {
  const { nodeData, nodes, flowId } = triggerEditorSignal;
  const { getTriggerNodeOutput } = triggerNodeOutputSignal;

  const dealProcessDefinition = (newNodes: any[]): any[] => {
    const processDefinitionJson = newNodes.map((item) => {
      const { outputs: nodeOutputs, initialData: nodeInitialData, ...restNodeData } = nodeData.value[item.id] || {};

      const output = getTriggerNodeOutput(item.id);
      if (item.blocks?.length) {
        const blocks = dealProcessDefinition(item.blocks);

        const data = {
          id: item.id,
          type: item.type,
          blocks,
          data: {
            ...restNodeData,
            // 覆写的属性写在后面
            title: item.data?.title
          },
          output: output
        };
        return data;
      } else {
        const data = {
          id: item.id,
          type: item.type,
          data: {
            ...restNodeData,
            // 覆写的属性写在后面
            title: item.data?.title
          },
          output: output
        };
        return data;
      }
    });
    return processDefinitionJson;
  };

  const handleSaveAndRelease = async (type: string) => {
    const processDefinitionJson = dealProcessDefinition(nodes.value);
    console.log('processDefinition', processDefinitionJson);

    const params = {
      id: flowId.value || '',
      processDefinition: JSON.stringify({ nodes: processDefinitionJson }),
      enableStatus: type === 'save' ? ProcessStatus.ORIGINAL : ProcessStatus.ENABLED
    };

    console.log('params', params);

    const res = await updateFlowMgmtDefinition(params);
    if (res) {
      Message.success(`${type === 'save' ? '保存' : '发布'}成功`);
    }
  };

  return (
    <div className={styles.flowEditorPage}>
      <div className={styles.header}>
        <Button type="primary" onClick={() => handleSaveAndRelease('save')}>
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

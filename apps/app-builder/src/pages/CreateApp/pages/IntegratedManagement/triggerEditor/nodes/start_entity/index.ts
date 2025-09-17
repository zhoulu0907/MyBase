import iconStart from '@/assets/flow/icon-start.jpg';
import { type FlowNodeRegistry } from '../../typings';
import { NodeType } from '../const';
import { formMeta } from './form-meta';

export const StartEntityNodeRegistry: FlowNodeRegistry = {
  type: NodeType.START_ENTITY,
  title: '表单(实体)触发节点',
  category: 'trigger',
  meta: {
    isStart: true, // Mark as start
    deleteDisable: true, // Start node cannot delete
    selectable: false, // Start node cannot select
    copyDisable: true, // Start node cannot copy
    expandable: false, // disable expanded
    addDisable: true // Start Node cannot be added
  },
  info: {
    icon: iconStart,
    description: '这是表单(实体)触发节点，用于设置启动工作流所需的信息。'
  },
  /**
   * Render node via formMeta
   */
  formMeta
};

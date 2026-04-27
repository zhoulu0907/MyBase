import iconStartTime from '@/assets/flow/nodes/startTime.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { formMeta } from './form-meta';

export const StartTimeNodeRegistry: FlowNodeRegistry = {
  type: NodeType.START_TIME,
  title: '定时触发节点',
  category: 'trigger',
  meta: {
    isStart: true, // Mark as start
    draggable: false, // 开始节点无法拖拽
    deleteDisable: true, // Start node cannot delete
    selectable: false, // Start node cannot select
    copyDisable: true, // Start node cannot copy
    expandable: false, // disable expanded
    addDisable: true // Start Node cannot be added
  },
  info: {
    icon: iconStartTime,
    description: '这是定时触发节点，用于设置启动工作流所需的信息。'
  },
  /**
   * Render node via formMeta
   */
  formMeta
};

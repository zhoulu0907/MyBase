import iconStartEntity from '@workflow/nodes/startEntity.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { formMeta } from './form-meta';

export const StartEntityNodeRegistry: FlowNodeRegistry = {
  type: NodeType.START_ENTITY,
  title: '表单(实体)触发节点',
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
    icon: iconStartEntity,
    description: '这是表单(实体)触发节点，用于设置启动工作流所需的信息。'
  },
  /**
   * Render node via formMeta
   */
  formMeta
};

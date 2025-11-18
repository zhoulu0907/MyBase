import iconStart from '@workflow/nodes/startDateField.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { formMeta } from './form-meta';

export const StartDateFieldNodeRegistry: FlowNodeRegistry = {
  type: NodeType.START_DATE_FIELD,
  title: '日期字段触发节点',
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
    icon: iconStart,
    description: '这是日期字段触发节点，用于设置启动工作流所需的信息。'
  },
  /**
   * Render node via formMeta
   */
  formMeta
};

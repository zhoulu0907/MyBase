import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const InputNodeRegistry: FlowNodeRegistry = {
  type: NodeType.START_API,
  title: '输入节点',
  category: 'input',
  meta: {
    isStart: true, // Mark as start
    draggable: false, // 开始节点无法拖拽
    deleteDisable: true, // Start node cannot delete
    selectable: false, // Start node cannot select
    copyDisable: true, // Start node cannot copy
    expandable: false, // disable expanded
    addDisable: true // Start Node cannot be added
  },
  info: {},
  /**
   * Render node via formMeta
   */
  formMeta
};

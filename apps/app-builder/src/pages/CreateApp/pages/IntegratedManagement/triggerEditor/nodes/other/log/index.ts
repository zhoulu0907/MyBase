import iconOther from '@workflow/nodes/log.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const LogNodeRegistry: FlowNodeRegistry = {
  type: NodeType.LOG,
  title: '日志节点',
  category: 'other',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false
  },
  info: {
    icon: iconOther,
    description: '这是日志节点，用于记录日志。'
  },
  /**
   * Render node via formMeta
   */
  formMeta,
  canDelete(ctx, node) {
    return true;
  },
  onAdd(ctx, from) {
    return {
      id: generateNodeId(NodeType.LOG),
      type: NodeType.LOG,
      data: {
        title: '日志节点'
      }
    };
  }
};

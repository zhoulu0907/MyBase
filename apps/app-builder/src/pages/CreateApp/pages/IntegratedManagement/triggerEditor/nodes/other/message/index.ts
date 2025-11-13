import iconOther from '@/assets/flow/nodes/message.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const MessageNodeRegistry: FlowNodeRegistry = {
  type: NodeType.MESSAGE,
  title: '消息节点',
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
    description: '这是消息节点，用于发送消息。'
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
      id: generateNodeId(NodeType.MESSAGE),
      type: NodeType.MESSAGE,
      data: {
        title: '消息节点'
      }
    };
  }
};

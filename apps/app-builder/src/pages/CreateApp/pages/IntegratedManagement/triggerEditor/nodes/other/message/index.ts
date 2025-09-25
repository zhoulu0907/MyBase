import iconStart from '@/assets/flow/icon-start.jpg';
import { v4 as uuidv4 } from 'uuid';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
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
    icon: iconStart,
    description: '这是消息节点，用于发送消息。'
  },
  /**
   * Render node via formMeta
   */
  formMeta,
  canDelete(ctx, node) {
    return node.parent !== ctx.document.root;
  },
  onAdd(ctx, from) {
    return {
      id: `message_${uuidv4()}`,
      type: 'message',
      data: {
        title: '消息节点'
      }
    };
  }
};

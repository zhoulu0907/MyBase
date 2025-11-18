import iconInteraction from '@/assets/flow/nodes/modal.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const ModalNodeRegistry: FlowNodeRegistry = {
  type: NodeType.MODAL,
  title: '弹窗节点',
  category: 'interaction',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false
  },
  info: {
    icon: iconInteraction,
    description: '这是弹窗节点，用于弹出弹窗。'
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
      id: generateNodeId(NodeType.MODAL),
      type: NodeType.MODAL,
      data: {
        title: '弹窗节点'
      }
    };
  }
};

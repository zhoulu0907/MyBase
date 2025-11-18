import iconInteraction from '@workflow/nodes/modal.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
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
    return node.parent !== ctx.document.root;
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

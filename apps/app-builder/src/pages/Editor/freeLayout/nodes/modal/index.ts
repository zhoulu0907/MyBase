import iconInteraction from '@workflow/nodes/modal.svg';
import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';

export const ModalNodeRegistry: FlowNodeRegistry = {
  type: 'modal',
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
      id: `modal_${nanoid(5)}`,
      type: 'modal',
      data: {
        title: '弹窗节点抽屉'
      }
    };
  }
};

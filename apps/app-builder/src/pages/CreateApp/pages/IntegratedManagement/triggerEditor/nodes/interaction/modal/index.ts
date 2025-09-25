import iconStart from '@/assets/flow/icon-start.jpg';
import { v4 as uuidv4 } from 'uuid';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
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
    icon: iconStart,
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
      id: `modal_${uuidv4()}`,
      type: 'modal',
      data: {
        title: '弹窗节点'
      }
    };
  }
};

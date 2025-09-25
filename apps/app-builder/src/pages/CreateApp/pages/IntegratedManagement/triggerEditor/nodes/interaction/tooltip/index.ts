import iconStart from '@/assets/flow/icon-start.jpg';
import { v4 as uuidv4 } from 'uuid';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { formMeta } from './form-meta';

export const TooltipNodeRegistry: FlowNodeRegistry = {
  type: NodeType.TOOLTIP,
  title: '提示节点',
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
    description: '这是提示节点，用于提示。'
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
      id: `tooltip_${uuidv4()}`,
      type: 'tooltip',
      data: {
        title: '提示节点'
      }
    };
  }
};

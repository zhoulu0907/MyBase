import iconInteraction from '@/assets/flow/nodes/tooltip.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
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
    icon: iconInteraction,
    description: '这是提示节点，用于提示。'
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
      id: generateNodeId(NodeType.TOOLTIP),
      type: NodeType.TOOLTIP,
      data: {
        title: '提示节点'
      }
    };
  }
};

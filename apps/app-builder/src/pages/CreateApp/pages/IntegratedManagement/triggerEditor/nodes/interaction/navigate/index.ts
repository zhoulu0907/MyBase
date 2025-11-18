import iconInteraction from '@/assets/flow/nodes/navigate.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const NavigateNodeRegistry: FlowNodeRegistry = {
  type: NodeType.NAVIGATE,
  title: '跳转节点',
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
    description: '这是跳转节点，用于跳转。'
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
      id: generateNodeId(NodeType.NAVIGATE),
      type: NodeType.NAVIGATE,
      data: {
        title: '跳转节点'
      }
    };
  }
};

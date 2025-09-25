import iconStart from '@/assets/flow/icon-start.jpg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const NagivateNodeRegistry: FlowNodeRegistry = {
  type: NodeType.NAGIVATE,
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
    icon: iconStart,
    description: '这是跳转节点，用于跳转。'
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
      id: generateNodeId(NodeType.NAGIVATE),
      type: NodeType.NAGIVATE,
      data: {
        title: '跳转节点'
      }
    };
  }
};

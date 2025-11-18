import iconInteraction from '@workflow/nodes/refresh.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const RefreshNodeRegistry: FlowNodeRegistry = {
  type: NodeType.REFRESH,
  title: '刷新节点',
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
    description: '这是刷新节点，用于刷新。'
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
      id: generateNodeId(NodeType.REFRESH),
      type: NodeType.REFRESH,
      data: {
        title: '刷新节点'
      }
    };
  }
};

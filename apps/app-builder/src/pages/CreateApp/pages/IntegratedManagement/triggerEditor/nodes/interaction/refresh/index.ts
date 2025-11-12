import iconInteraction from '@/assets/flow/nodes/refresh.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
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
    return true;
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

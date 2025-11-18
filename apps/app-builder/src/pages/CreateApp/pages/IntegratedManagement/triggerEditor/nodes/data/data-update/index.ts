import iconData from '@/assets/flow/nodes/dataUpdate.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const DataUpdateNodeRegistry: FlowNodeRegistry = {
  type: NodeType.DATA_UPDATE,
  title: '数据更新节点',
  category: 'data',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false
  },
  info: {
    icon: iconData,
    description: '这是数据更新节点，用于更新数据。'
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
      id: generateNodeId(NodeType.DATA_UPDATE),
      type: NodeType.DATA_UPDATE,
      data: {
        title: '数据更新节点'
      }
    };
  }
};

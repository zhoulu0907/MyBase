import iconData from '@/assets/flow/nodes/dataAdd.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const DataAddNodeRegistry: FlowNodeRegistry = {
  type: NodeType.DATA_ADD,
  title: '数据新增节点',
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
    description: '这是数据新增节点，用于新增数据。'
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
      id: generateNodeId(NodeType.DATA_ADD),
      type: NodeType.DATA_ADD,
      data: {
        title: '数据新增节点'
      }
    };
  }
};

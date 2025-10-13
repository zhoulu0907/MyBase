import iconData from '@/assets/flow/nodes/dataDelete.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const DataDeleteNodeRegistry: FlowNodeRegistry = {
  type: NodeType.DATA_DELETE,
  title: '数据删除节点',
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
    description: '这是数据删除节点，用于删除数据。'
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
      id: generateNodeId(NodeType.DATA_DELETE),
      type: NodeType.DATA_DELETE,
      data: {
        title: '数据删除节点'
      }
    };
  }
};

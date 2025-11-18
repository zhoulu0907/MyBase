import iconData from '@workflow/nodes/dataQueryMultiple.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const DataQueryMultipleNodeRegistry: FlowNodeRegistry = {
  type: NodeType.DATA_QUERY_MULTIPLE,
  title: '数据查询节点(多条)',
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
    description: '这是数据查询节点(多条)，用于查询数据。'
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
      id: generateNodeId(NodeType.DATA_QUERY_MULTIPLE),
      type: NodeType.DATA_QUERY_MULTIPLE,
      data: {
        title: '数据查询节点(多条)'
      }
    };
  }
};

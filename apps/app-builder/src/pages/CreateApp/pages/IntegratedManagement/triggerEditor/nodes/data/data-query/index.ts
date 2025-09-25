import iconStart from '@/assets/flow/icon-start.jpg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const DataQueryNodeRegistry: FlowNodeRegistry = {
  type: NodeType.DATA_QUERY,
  title: '数据查询节点(单条)',
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
    icon: iconStart,
    description: '这是数据查询节点(单条)，用于查询数据。'
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
      id: generateNodeId(NodeType.DATA_QUERY),
      type: NodeType.DATA_QUERY,
      data: {
        title: '数据查询节点(单条)'
      }
    };
  }
};

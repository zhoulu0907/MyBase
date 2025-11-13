import iconOther from '@/assets/flow/nodes/dataMapper.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const DataMapperNodeRegistry: FlowNodeRegistry = {
  type: NodeType.DATA_MAPPER,
  title: '数据映射节点',
  category: 'other',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false
  },
  info: {
    icon: iconOther,
    description: '这是数据映射节点，用于映射数据。'
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
      id: generateNodeId(NodeType.DATA_MAPPER),
      type: NodeType.DATA_MAPPER,
      data: {
        title: '数据映射节点'
      }
    };
  }
};

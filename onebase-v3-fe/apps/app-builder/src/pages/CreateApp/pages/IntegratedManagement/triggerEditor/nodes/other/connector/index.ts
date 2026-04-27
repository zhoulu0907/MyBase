import iconOther from '@/assets/flow/nodes/log.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const ConnectorNodeRegistry: FlowNodeRegistry = {
  type: NodeType.CONNECTOR,
  title: '连接器节点',
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
    description: '这是连接器节点，用于记录日志。'
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
      id: generateNodeId(NodeType.CONNECTOR),
      type: NodeType.CONNECTOR,
      data: {
        title: '连接器节点'
      }
    };
  }
};

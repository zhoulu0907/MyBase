import iconOther from '@/assets/flow/nodes/ipaas.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const JavascriptNodeRegistry: FlowNodeRegistry = {
  type: NodeType.JavaScript,
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
    description: '这是连接器节点，用于连接各类API'
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
      id: generateNodeId(NodeType.JavaScript),
      type: NodeType.JavaScript,
      data: {
        title: 'JS脚本节点'
      }
    };
  }
};

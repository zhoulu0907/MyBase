import iconOther from '@workflow/nodes/json.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const JsonNodeRegistry: FlowNodeRegistry = {
  type: NodeType.JSON,
  title: 'JSON节点',
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
    description: '这是JSON节点，用于解析JSON数据。'
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
      id: generateNodeId(NodeType.JSON),
      type: NodeType.JSON,
      data: {
        title: 'JSON节点'
      }
    };
  }
};

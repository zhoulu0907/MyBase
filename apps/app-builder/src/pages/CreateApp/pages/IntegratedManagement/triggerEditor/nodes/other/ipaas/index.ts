import iconOther from '@workflow/nodes/ipaas.svg';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '@onebase/common';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const IpaasNodeRegistry: FlowNodeRegistry = {
  type: NodeType.IPAAS,
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
    return node.parent !== ctx.document.root;
  },
  onAdd(ctx, from) {
    return {
      id: generateNodeId(NodeType.IPAAS),
      type: NodeType.IPAAS,
      data: {
        title: '连接器节点'
      }
    };
  }
};

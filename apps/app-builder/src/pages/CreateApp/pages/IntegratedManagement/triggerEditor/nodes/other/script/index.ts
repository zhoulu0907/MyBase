import iconOther from '@/assets/flow/nodes/script.svg';
import { NodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { generateNodeId } from '../../utils';
import { formMeta } from './form-meta';

export const ScriptNodeRegistry: FlowNodeRegistry = {
  type: NodeType.SCRIPT,
  title: '脚本节点',
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
    description: '这是脚本节点，用于执行脚本。'
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
      id: generateNodeId(NodeType.SCRIPT),
      type: NodeType.SCRIPT,
      data: {
        title: '脚本节点'
      }
    };
  }
};

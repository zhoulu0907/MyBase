import iconStart from '@/assets/flow/icon-start.jpg';
import { nanoid } from 'nanoid';
import { type FlowNodeRegistry } from '../../../typings';
import { NodeType } from '../../const';
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
    icon: iconStart,
    description: '这是脚本节点，用于执行脚本。'
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
      id: `script_${nanoid()}`,
      type: 'script',
      data: {
        title: '脚本节点'
      }
    };
  }
};

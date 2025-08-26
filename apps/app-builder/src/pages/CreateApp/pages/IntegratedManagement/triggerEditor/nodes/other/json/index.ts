import iconStart from '@/assets/flow/icon-start.jpg';
import { nanoid } from 'nanoid';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const JsonNodeRegistry: FlowNodeRegistry = {
  type: 'json',
  title: 'JSON节点',
  category: 'other',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false,
  },
  info: {
    icon: iconStart,
    description:
    '这是JSON节点，用于解析JSON数据。',
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
      id: `json_${nanoid()}`,
      type: 'json',
      data: {
        title: 'JSON节点',
      },
    };
  },
};

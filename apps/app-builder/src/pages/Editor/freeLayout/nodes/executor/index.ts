import iconInteraction from '@/assets/flow/nodes/modal.svg';
import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';

export const ExecutorNodeRegistry: FlowNodeRegistry = {
  type: 'executor',
  title: '执行人',
  category: 'interaction',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false
  },
  info: {
    icon: iconInteraction,
    description: '这是执行人节点，用于编辑执行人。'
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
      id: `executor_${nanoid(5)}`,
      type: 'executor',
      data: {
        title: '执行人抽屉'
      }
    };
  }
};

import iconInteraction from '@/assets/flow/nodes/modal.svg';
import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';

export const ApproverNodeRegistry: FlowNodeRegistry = {
  type: 'approver',
  title: '审批人',
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
    description: '这是审批人节点，用于编辑审批人。'
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
        title: '审批人抽屉'
      }
    };
  }
};

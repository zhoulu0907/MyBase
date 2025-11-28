import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';
import approver from '../../assets/bpmLogo/approver.png';

export const ApproverNodeRegistry: FlowNodeRegistry = {
  type: 'approver',
  name: '审批人',
  category: 'interaction',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false,
    defaultPorts: [
      { type: 'output', location: 'bottom' },
      { type: 'input', location: 'top' }
    ]
  },
  info: {
    icon: approver,
    description: '这是审批人节点，用于编辑审批人。'
  },
  /**
   * Render node via formMeta
   */
  formMeta,
  canDelete(ctx, node) {
    return node.parent !== ctx.document.root;
  },
  onAdd() {
    return {
      id: `executor_${nanoid(5)}`,
      type: 'executor',
      data: {
        name: '审批人'
      }
    };
  }
};

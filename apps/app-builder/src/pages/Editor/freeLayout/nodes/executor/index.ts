import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';
import executor_big from '../../assets/bpmLogo/executor_big.png';

export const ExecutorNodeRegistry: FlowNodeRegistry = {
  type: 'executor',
  name: '执行人',
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
    icon: executor_big,
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
        name: '执行人'
      }
    };
  }
};

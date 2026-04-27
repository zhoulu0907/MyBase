import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';
import parallelBranch from '../../assets/bpmLogo/parallel_branch.png';
import { WorkflowNodeType } from '../constants';

export const ParallelBranchNodeRegistry: FlowNodeRegistry = {
  type: WorkflowNodeType.BRANCH_IN,
  name: '并行分支',
  category: 'interaction',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false,
    defaultPorts: [
      { type: 'input', portID: 'input-top', location: 'top' },
      { type: 'output', portID: 'output-left', location: 'left' },
      { type: 'output', portID: 'output-right', location: 'right' },
      { type: 'output', portID: 'output-bottom', location: 'bottom' }
    ]
  },
  info: {
    icon: parallelBranch,
    description: '并行分支'
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
        name: '并行分支'
      }
    };
  }
};

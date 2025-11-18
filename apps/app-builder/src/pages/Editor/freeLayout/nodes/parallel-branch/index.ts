import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';
import parallelBranch from '../../assets/bpmLogo/parallel_branch.png';
import { WorkflowNodeType } from '../constants';

export const ParallelBranchNodeRegistry: FlowNodeRegistry = {
  type: WorkflowNodeType.PARALLEL_BRANCH,
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
      id: `parallel_branch_${nanoid(5)}`,
      type: WorkflowNodeType.BRANCH_IN,
      data: {
        name: '并行分支'
      }
    };
  }
};

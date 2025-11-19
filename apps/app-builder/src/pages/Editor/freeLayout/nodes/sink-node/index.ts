import { type FlowNodeRegistry } from '../../typings';
import { formMeta } from './form-meta';
import { nanoid } from 'nanoid';
import sinkNodeBranch from '../../assets/bpmLogo/sink_node.png';
import { WorkflowNodeType } from '../constants';

export const SinkNodeBranchNodeRegistry: FlowNodeRegistry = {
  type: WorkflowNodeType.SINK_NODE_BRANCH,
  name: '汇聚节点',
  category: 'interaction',
  meta: {
    isStart: false,
    deleteDisable: false,
    selectable: true,
    copyDisable: false,
    expandable: false,
    addDisable: false,
    defaultPorts: [
      { type: 'input', location: 'top' },
      { type: 'output', location: 'bottom' }
    ]
  },
  info: {
    icon: sinkNodeBranch,
    description: '汇聚节点'
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
      id: `sink_node_branch_${nanoid(5)}`,
      type: WorkflowNodeType.BRANCH_IN,
      data: {
        name: '汇聚节点'
      }
    };
  }
};

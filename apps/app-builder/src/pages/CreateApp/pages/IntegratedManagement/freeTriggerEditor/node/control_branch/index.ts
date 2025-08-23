import iconStart from '@/assets/flow/icon-start.jpg';
import { nanoid } from 'nanoid';
import { type FlowNodeRegistry } from '../../typings';
import { WorkflowNode } from '../constants';
import { formMeta } from './form-meta';

export const ControlBranchNodeRegistry: FlowNodeRegistry = {
    type: WorkflowNode.ControlBranch,
    meta: {
      isStart: false,
      deleteDisable: false,
      copyDisable: false,
      nodePanelVisible: true,
      defaultPorts: [{ type: 'output' }, { type: 'input' }],
      size: {
        width: 360,
        height: 211,
      },
    },
    info: {
      icon: iconStart,
      description: '分支节点',
    },
    /**
     * Render node via formMeta
     */
    formMeta,
    /**
     * Start Node cannot be added
     */
    canAdd() {
      return true;
    },
    onAdd() {
        return {
            id: `condition_${nanoid(5)}`,
            type: WorkflowNode.ControlBranch,
            data: {
                title: '分支节点',
                conditions: [
                    {
                      key: `if_${nanoid(5)}`,
                      value: {},
                    },
                    {
                      key: `if_${nanoid(5)}`,
                      value: {},
                    },
                  ],
            }
        }
    }
  };
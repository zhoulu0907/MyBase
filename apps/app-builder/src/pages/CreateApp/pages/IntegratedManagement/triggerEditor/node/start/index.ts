import iconStart from '@/assets/flow/icon-start.jpg';
import { type FlowNodeRegistry } from '../../typings';
import { WorkflowNodeType } from '../constants';
import { formMeta } from './form-meta';

export const StartNodeRegistry: FlowNodeRegistry = {
    type: WorkflowNodeType.Start,
    meta: {
      isStart: true,
      deleteDisable: true,
      copyDisable: true,
      nodePanelVisible: false,
      defaultPorts: [{ type: 'output' }],
      size: {
        width: 360,
        height: 211,
      },
    },
    info: {
      icon: iconStart,
      description: '开始节点',
    },
    /**
     * Render node via formMeta
     */
    formMeta,
    /**
     * Start Node cannot be added
     */
    canAdd() {
      return false;
    },
  };
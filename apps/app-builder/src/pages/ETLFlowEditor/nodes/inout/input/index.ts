import { ETLNodeType } from '@onebase/common';
import { type FlowNodeRegistry } from '../../../typings';
import { formMeta } from './form-meta';

export const InputNodeRegistry: FlowNodeRegistry = {
  type: ETLNodeType.INPUT_NODE,
  title: '输入节点',
  meta: {
    isStart: true,
    draggable: true,
    deleteDisable: false,
    selectable: true,
    defaultPorts: [{ type: 'output' }]
  },
  /**
   * Render node via formMeta
   */
  formMeta
};
